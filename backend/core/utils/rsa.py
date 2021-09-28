from base64 import b64decode, b64encode
from binascii import Error as binasciiError
from Crypto.Hash import SHA256
from Crypto.Cipher import PKCS1_v1_5 as Cipher_pkcs1_v1_5
from Crypto.Signature import PKCS1_v1_5 as Signature_pkcs1_v1_5
from Crypto.PublicKey import RSA


def rsa_import_key_pem(file_path):
    rsa_object = RSA.import_key(open(file_path, 'r').read())
    return rsa_object


# response middleware sign
server_sign_pub_key = rsa_import_key_pem('')
server_sign_pri_key = rsa_import_key_pem('')


def en(pub_key, text):
    cipher = Cipher_pkcs1_v1_5.new(pub_key)
    cipher_text = cipher.encrypt(text.encode())
    return b64encode(cipher_text).decode()


def de(pri_key, text):
    try:
        cipher_text = b64decode(text)
    except binasciiError:
        return ''
    else:
        cipher = Cipher_pkcs1_v1_5.new(pri_key)
        try:
            message = cipher.decrypt(cipher_text, None)
        except ValueError:
            return ''
        else:
            return message.decode() if message else ''


def sign(pri_key, text):
    msg_hash = SHA256.new(b64encode(text.encode()))
    return b64encode(Signature_pkcs1_v1_5.new(pri_key).sign(msg_hash))


def verify(pub_key, text_hash, signature):
    try:
        return Signature_pkcs1_v1_5.new(pub_key).verify(text_hash, b64decode(signature))
    except (ValueError, TypeError):
        return False

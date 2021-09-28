import pyotp


def gen_mfa_secret_key():
    return pyotp.random_base32()


def is_mfa_code_ok(secret_key, mfa_code):
    return pyotp.TOTP(secret_key).verify(mfa_code)

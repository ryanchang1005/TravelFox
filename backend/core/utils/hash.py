import hashlib


def sha256(data):
    return hashlib.sha256(data.encode('utf-8')).hexdigest()


def md5(data):
    return hashlib.md5(data.encode('utf-8')).hexdigest()


def sha1(data):
    return hashlib.sha1(data.encode('utf-8')).hexdigest()

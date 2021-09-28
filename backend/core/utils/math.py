import math
from decimal import Decimal


def floor(n, x):
    """
    無條件捨去到小數點後x位
    :param n: 值: 0.12345678
    :param x: 後x位: 4
    :return: 0.1234
    """
    return math.floor(Decimal(str(n)) * Decimal(10) ** Decimal(x)) / 10.0 ** x

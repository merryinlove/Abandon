package com.csu.utils;/*
* 根据firebug调试结果与js参考写出,部分代码与js一致
* 顺便吐槽下,卧槽,这js写的太难懂了
* */


public class RSAUtil {
    private BigInt key_e = new BigInt();
    private BigInt this_bkplus1 = new BigInt();
    private BigInt this_modulus = new BigInt();
    private BigInt this_mu = new BigInt();

    private int Number(boolean value) {
        return value ? 1 : 0;
    }

    private void arrayCopy(int[] src, int srcStart, int[] dest, int destStart, int n) {
        int m = Math.min(srcStart + n, src.length);
        for (int i = srcStart, j = destStart; i < m; ++i, ++j) {
            dest[j] = src[i];
        }
    }

    private BigInt biAdd(BigInt x, BigInt y) {
        BigInt result;

        if (x.isNeg != y.isNeg) {
            y.isNeg = !y.isNeg;
            result = biSubtract(x, y);
            y.isNeg = !y.isNeg;
        } else {
            result = new BigInt();
            int c = 0;
            int n;
            for (int i = 0; i < x.digits.length; ++i) {
                n = x.digits[i] + y.digits[i] + c;
                result.digits[i] = n % 65536;
                c = Number(n >= 65536);
            }
            result.isNeg = x.isNeg;
        }
        return result;
    }

    private int biCompare(BigInt x, BigInt y) {
        if (x.isNeg != y.isNeg) {
            return 1 - 2 * Number(x.isNeg);
        }
        for (int i = x.digits.length - 1; i >= 0; --i) {
            if (x.digits[i] != y.digits[i]) {
                if (x.isNeg) {
                    return 1 - 2 * Number(x.digits[i] > y.digits[i]);
                } else {
                    return 1 - 2 * Number(x.digits[i] < y.digits[i]);
                }
            }
        }
        return 0;
    }

    private BigInt biDivideByRadixPower(BigInt x, int n) {
        BigInt result = new BigInt();
        arrayCopy(x.digits, n, result.digits, 0, result.digits.length - n);
        return result;
    }

    private int biHighIndex(BigInt value) {
        int result = value.digits.length - 1;
        while (result > 0 && value.digits[result] == 0) --result;
        return result;
    }

    private BigInt biModuloByRadixPower(BigInt x, int n) {
        BigInt result = new BigInt();
        arrayCopy(x.digits, 0, result.digits, 0, n);
        return result;
    }

    private BigInt biMultiply(BigInt x, BigInt y) {
        BigInt result = new BigInt();
        int c;
        int n = biHighIndex(x);
        int t = biHighIndex(y);
        int uv, k;

        for (int i = 0; i <= t; ++i) {
            c = 0;
            k = i;
            for (int j = 0; j <= n; ++j, ++k) {
                uv = result.digits[k] + x.digits[j] * y.digits[i] + c;
                result.digits[k] = uv & 65535;
                c = uv >>> 16;
                //c = Math.floor(uv / biRadix);
            }
            result.digits[i + n + 1] = c;
        }
        // Someone give me a logical xor, please.
        result.isNeg = x.isNeg != y.isNeg;
        return result;
    }

    private BigInt biShiftRight(BigInt x, int n) {
        int[] lowBitMasks = {0x0000, 0x0001, 0x0003, 0x0007, 0x000F, 0x001F,
                0x003F, 0x007F, 0x00FF, 0x01FF, 0x03FF, 0x07FF,
                0x0FFF, 0x1FFF, 0x3FFF, 0x7FFF, 0xFFFF};
        int bitsPerDigit = 16;
        int digitCount = (int) Math.floor(n / bitsPerDigit);
        BigInt result = new BigInt();
        arrayCopy(x.digits, digitCount, result.digits, 0,
                x.digits.length - digitCount);
        int bits = n % bitsPerDigit;
        int leftBits = bitsPerDigit - bits;
        for (int i = 0, i1 = i + 1; i < result.digits.length - 1; ++i, ++i1) {
            result.digits[i] = (result.digits[i] >>> bits) |
                    ((result.digits[i1] & lowBitMasks[bits]) << leftBits);
        }
        result.digits[result.digits.length - 1] >>>= bits;
        result.isNeg = x.isNeg;
        return result;
    }

    private BigInt biSubtract(BigInt x, BigInt y) {
        BigInt result;
        if (x.isNeg != y.isNeg) {
            y.isNeg = !y.isNeg;
            result = biAdd(x, y);
            y.isNeg = !y.isNeg;
        } else {
            result = new BigInt();
            int n, c;
            c = 0;
            for (int i = 0; i < x.digits.length; ++i) {
                n = x.digits[i] - y.digits[i] + c;
                result.digits[i] = n % 65536;
                // Stupid non-conforming modulus operation.
                if (result.digits[i] < 0) result.digits[i] += 65536;
                c = 0 - Number(n < 0);
            }
            // Fix up the negative sign, if any.
            if (c == -1) {
                c = 0;
                for (int i = 0; i < x.digits.length; ++i) {
                    n = 0 - result.digits[i] + c;
                    result.digits[i] = n % 65536;
                    // Stupid non-conforming modulus operation.
                    if (result.digits[i] < 0) result.digits[i] += 65536;
                    c = 0 - Number(n < 0);
                }
                // Result is opposite sign of arguments.
                result.isNeg = !x.isNeg;
            } else {
                // Result is same sign.
                result.isNeg = x.isNeg;
            }
        }
        return result;
    }

    private String biToHex(BigInt x) {
        String result = "";
        for (int i = biHighIndex(x); i > -1; --i) {
            result += digitToHex(x.digits[i]);
        }
        return result;
    }


    private String digitToHex(int n) {
        char[] hexToChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        int mask = 0xf;
        String result = "";
        for (int i = 0; i < 4; ++i) {
            result += hexToChar[n & mask];
            n >>>= 4;
        }
        return reverseStr(result);
    }

    private String encrypt(String value) {
        //填充块大小至到被chunkSize整除,填充0
        //默认chunkSize = 126
        int chunkSize = 126;
        char[] character = new char[chunkSize];

        int point;
        for (point = 0; point < value.length(); point++) {
            character[point] = value.charAt(point);
        }
        if (value.length() % 126 != 0) {
            for (int i = 0; i < chunkSize - value.length(); i++) {
                character[point++] = 0;
            }
        }
        String result = "";
        for (int i = 0; i < chunkSize; i += chunkSize) {
            BigInt block = new BigInt();
            int k = 0;
            for (int j = i; j < i + chunkSize; ++k) {
                block.digits[k] = character[j++];
                block.digits[k] += character[j++] << 8;
            }
            result = result + biToHex(powMod(block, key_e)) + " ";
        }
        return result.substring(0, result.length() - 1);
    }

    public String getEncrypt(String value) {
        initial();
        return encrypt(value);
    }

    private void initial() {
        key_e.digits[0] = 1;
        key_e.digits[1] = 1;

        //这是module的计算结果,可以根据firebug看出
        int[] modulus = {13381, 11781, 7242, 62287, 38208, 17148, 36682, 28526, 54813, 37809, 12443, 12925, 27820, 1386, 48192, 27208, 27855, 59049, 15631, 22431, 16025, 50103, 14892, 62276, 15625, 2557, 36289, 11124, 13811, 61295, 13727, 46513, 31398, 35195, 60201, 63116, 46142, 15347, 18170, 44287, 40478, 56849, 61724, 30076, 33833, 20967, 42674, 19595, 37418, 23451, 46539, 6410, 33740, 39386, 9150, 17236, 19320, 30791, 25100, 51856, 54224, 7506, 11138, 43168};
        int[] mu = new int[]{64960, 5794, 58342, 4906, 21255, 5449, 37131, 1520, 25147, 48127, 15949, 8219, 32169, 50821, 10190, 27537, 14250, 44660, 20599, 33058, 37701, 20652, 45979, 39567, 20286, 30758, 17065, 20293, 58918, 47896, 54663, 31950, 53677, 57426, 6549, 19452, 14672, 16668, 37652, 22641, 44716, 5187, 8214, 50719, 46975, 33250, 60475, 56069, 45892, 11879, 33516, 19745, 41990, 3787, 2709, 62927, 59461, 1988, 64302, 21380, 25436, 62171, 55507, 33957, 1};

        System.arraycopy(modulus, 0, this_modulus.digits, 0, 64);
        System.arraycopy(mu, 0, this_mu.digits, 0, 65);
    }

    private BigInt modulo(BigInt x) {

        int k = biHighIndex(this_modulus) + 1;
        BigInt r = biSubtract(biModuloByRadixPower(x, k + 1),
                biModuloByRadixPower(
                        biMultiply(biDivideByRadixPower(
                                biMultiply(biDivideByRadixPower(x, k - 1),
                                        this_mu), k + 1), this_modulus), k + 1));
        if (r.isNeg) {
            r = biAdd(r, this_bkplus1);
        }
        boolean pin = biCompare(r, this_modulus) >= 0;
        while (pin) {
            r = biSubtract(r, this_modulus);
            pin = biCompare(r, this_modulus) >= 0;
        }
        return r;
    }

    private BigInt multiplyMod(BigInt result, BigInt block) {
        return modulo(biMultiply(result, block));
    }

    private BigInt powMod(BigInt block, BigInt e) {
        /*
        * 　m^e ≡ c (mod n)
        *    c  加密后
        *    m 带加密字符
        *    e 65537/hex1003
        *    n 公钥
        * */
        BigInt result = new BigInt();
        result.digits[0] = 1;
        BigInt a = block;
        BigInt k = e;
        while (true) {
            if ((k.digits[0] & 1) != 0) result = this.multiplyMod(result, a);
            k = biShiftRight(k, 1);
            if (k.digits[0] == 0 && biHighIndex(k) == 0) break;
            a = this.multiplyMod(a, a);
        }
        return result;
    }

    private String reverseStr(String value) {
        return new StringBuilder(value).reverse().toString();
    }

    private class BigInt {
        private int[] digits = new int[130];
        private boolean isNeg = false;
    }
}




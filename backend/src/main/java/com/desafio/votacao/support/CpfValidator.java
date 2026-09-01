package com.desafio.votacao.support;

public final class CpfValidator {

    private CpfValidator() {
    }

    public static String normalizar(String cpf) {
        return cpf == null ? "" : cpf.replaceAll("\\D", "");
    }

    public static boolean isValido(String cpf) {
        String digitos = normalizar(cpf);
        if (digitos.length() != 11 || digitos.chars().distinct().count() == 1) {
            return false;
        }
        return digitoVerificador(digitos, 9) == charToInt(digitos, 9)
            && digitoVerificador(digitos, 10) == charToInt(digitos, 10);
    }

    private static int digitoVerificador(String digitos, int posicao) {
        int soma = 0;
        int peso = posicao + 1;
        for (int i = 0; i < posicao; i++) {
            soma += charToInt(digitos, i) * peso--;
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private static int charToInt(String digitos, int index) {
        return digitos.charAt(index) - '0';
    }
}

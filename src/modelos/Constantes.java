/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package modelos;

import static java.lang.Math.pow;
import static java.lang.Math.round;
import java.util.ArrayList;


public abstract class Constantes
{
    // TIPOS    
    /** PI sem tipo definido ainda. */
    public static final int VAZIO = -1;
    /** PI NUMERICO, de valor INTEIRO/DISCRETO ou REAL/CONTINUO - Pode ser NB, HB ou LB. */
    public static final int NUMERICO = 1;
    /** PI de valor CATEGÓRICO SIMPLES - Uma palavra - Pode ser não ordenado (Pode ser NB, HT, LT ou HLT) ou não (Sempre NB). */
    public static final int CAT_SIMP = 2;
    /** Indicador de valor CATEGÓRICO COMPOSTO - Um conjunto de palavras separadas por vírgula - Palavra 1, Palavra 2,..., Palavra n - Sempre NB. */
    public static final int CAT_COMP = 3;
        
    // COMPORTAMENTOS
    /** Utiliza o padrão cadastrado no banco de dados. */
    public static final int DB = -1;
    /** Nominal é o melhor (default) - Utilização, por exemplo. */
    public static final int NB = 0;
    /** Maior é melhor - Memória, desempenho, transferencia de dados, etc. */
    public static final int HB = 1;
    /** Menor é o melhor - Custo, despesas, delay, etc. */
    public static final int LB = 2;
    /** Para indicadores categóricos simples: Maiores são toleráveis. */
    public static final int HT = 3;
    /** Para indicadores categóricos simples: Menores são toleráveis. */
    public static final int LT = 4;
    /** Para indicadores categóricos simples: Maiores e menores são toleráveis. */
    public static final int HLT = 5;
    
    /************************** FUNÇÕES AUXILIARES *****************************/
    /**
     * Imprime o tipo do PI correspondente ao número passado por parâmetro.
     * @param tipo O valor numérico do tipo do PI.
     * @return O valor String do tipo do PI.
     */
    public final static String imprimeTipo(int tipo)
    {
        switch(tipo)
        {
            case NB:
                return "NB";
            case HB:
                return "HB";
            case LB:
                return "LB";
            case HT:
                return "HT";
            case LT:
                return "LT";
            case HLT:
                return "HLT";
            default:
                return "??";
        }
    }
    /**
     * Verifica se a String é um número.
     * @param s String a se verifiar.
     * @return True se for numero; false, caso contrário.
     */
    public final static boolean ehNumero(String s)
    {
        try {
            Double.parseDouble(s);
            return true;
        }
        catch(NumberFormatException ex) {
            return false;
        }
    }
    /**
     * Verifica se a String possui só números e letras.
     * @param s String a se verifiar.
     * @return "True" se for contituida só por números e letras; "false", caso contrário.
     */
    public final static boolean ehStringAlfaNumerica(String s)
    {
        for(int i = 0; i < s.length(); i++)
            if( !Character.isAlphabetic(s.charAt(i)) && !Character.isDigit(s.charAt(i)) )
                return false;
        
        return true;
    }
    
    /**
     * Imprime o vetor booleano passado com 0s e 1s.
     * @param vet Vetor booleano.
     */
    public final static void imprimeVet(boolean[] vet)
    {
        if(vet != null)
        {
            for(int i = 0; i < vet.length; i++)
                System.out.print(vet[i] ? "1 ":"0 ");
            System.out.println();
        }        
    }
    /**
     * Imprime os provedores codificados pelo vetor de booleanos passados.
     * @param vet O vetor binario de provedores codificados.
     */
    public final static void imprimeProvCod(boolean[] vet)
    {
        for(int i = 0; i < vet.length; i++)
            if(vet[i])
                System.out.print("P" + (i+1) + "\t");
        
        System.out.println();
    }
    /**   
     * @param vet O vetor binario de provedores codificados.
     * @return  A String que representa os provedores codificados pelo vetor de booleanos passados.
     */
    public final static String provCod(boolean[] vet)
    {
        String ret = "";
        for(int i = 0; i < vet.length; i++)
            if(vet[i])
                ret += ("P" + (i+1) + "\t");        
        
        return ret;
    }
    
    /**
     * Verifica se o array de vetores de booleanos contem o vetor passado.
     * @param array Array de vetores de booleanos.
     * @param vet Vetor de booleanos a ser testado.
     * @return "true" caso contenha, "false", caso contrario.
     */
    public final static boolean contem(ArrayList<boolean[]> array, boolean[] vet)
    {
        int i, j, flag;
        boolean[] aux;        
        
        for(i = 0; i < array.size(); i++)
        {
            flag = 0;
            aux = array.get(i);
            if(aux.length == vet.length)
            {
                for(j = 0; j < aux.length; j++)                
                    if(aux[j] == vet[j])  flag++;  else  break;
                
                if(flag == vet.length)
                    return true;
            }
        }
        return false;
    }
    
    /**
     * Função de arredondamento automático.
     * @param numero Número a ser arredondado.
     * @param casaDecimal A casa decimal ser arredondada.
     * @return O Número arredondado.
     */
    public final static double arredonda(double numero, int casaDecimal)
    {
        if(numero == 0.0 || casaDecimal < 0)
            return 0.0;
        if(casaDecimal == 0)
            return round(numero); 
        // casaDecimal > 0
        numero = numero * pow(10, casaDecimal);        
        numero = round(numero);        
        return (numero / pow(10, casaDecimal));
    }
}

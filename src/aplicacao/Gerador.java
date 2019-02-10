/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package aplicacao;

import java.util.Random;
import java.util.Scanner; 
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
//import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import static modelos.Constantes.*;

public abstract class Gerador
{  
    private static final int MIN = 1;    
    private static final int MAX = 20;
    private static final int MEDIO = MIN + (MAX - MIN)/2;
    
    private static final double PESO_MIN = 0.1;
    private static final double PESO_MAX = 3.0;
    
    private static final double CUSTO_MIN = 0.5;
    private static final double CUSTO_MAX = 15.0;    
    
    public static void main(String args[])
    {        
        int i, j, opc, quantKPI, quantProv;        
        String nomeArq, dados;
        FileWriter fw = null;            
        BufferedWriter bw = null;
        Scanner ler = new Scanner(System.in);        
        
        System.out.println("# # # # # # Gerador aleatório de Dados # # # # # #\n");
        while(true) 
        {
            try {
                System.out.print("\n1) Nova requisição;\n2) Novo banco de dados;\n0) Sair.\n\nOpcao: ");
                opc = ler.nextInt();
                if(opc == 0)
                    System.exit(0);
                System.out.println();
                switch(opc)
                {                    
                    case 1: // Requisição aleatória   
                        // Arquivo para escrita
                        do {
                            System.out.print("Nome do arquivo a ser escrito: ");
                            nomeArq = ler.next();
                            try {
                                fw = new FileWriter(nomeArq);   
                                bw = new BufferedWriter(fw);
                            }
                            catch(IOException ex)
                            {
                                System.out.println("Erro com o arquivo: " + ex.getMessage());                                 
                                nomeArq = null;
                            }
                        } while(nomeArq == null || nomeArq.isEmpty() || fw == null || bw == null);
                        // Quantidade de KPIs
                        do {
                            System.out.print("Quantidade de KPIs a serem escritos: ");
                            quantKPI = ler.nextInt();
                            System.out.println();
                        } while(quantKPI <= 0);
                        try { 
                            // Nomes padrões para os KPIs
                            dados = "kpi 1";
                            for(i = 1; i < quantKPI; i++)                        
                                dados += ", kpi " + (i+1);                            
                            bw.append(dados);
                            bw.newLine();
                            // Valores aleatórios para os KPIs
                            dados = String.valueOf(geraRandomInt(MIN, MAX));
                            for(i = 1; i < quantKPI; i++)                            
                                dados += ", " + geraRandomInt(MIN, MAX);
                            
                            bw.append(dados);
                            bw.newLine();
                            // Pesos aleatórios para os KPIs
                            dados = String.valueOf(arredonda(geraRandomReal(PESO_MIN, PESO_MAX), 1));
                            for(i = 1; i < quantKPI; i++)
                                dados += ", " + arredonda(geraRandomReal(PESO_MIN, PESO_MAX), 1);
                            bw.append(dados);
                            bw.close();
                            System.out.println("\n# # # # FINALIZADO! # # # #\n");
                        }
                        catch(IOException ex)
                        {
                            System.out.println("Erro com o arquivo: " + ex.getMessage());                             
                        }
                        break;
        ///////////////////////////////////////////////////////////////////////////
                    case 2: // Banco de dados aleatório
                        do {
                            System.out.print("Nome do arquivo a ser escrito: ");
                            nomeArq = ler.next();
                            try {
                                fw = new FileWriter(nomeArq);   
                                bw = new BufferedWriter(fw);
                            }
                            catch(IOException ex)
                            {
                                System.out.println("Erro com o arquivo: " + ex.getMessage());                                
                                nomeArq = null;
                            }
                        } while(nomeArq == null || nomeArq.isEmpty() || fw == null || bw == null);
                        do {
                            System.out.print("Quantidade de KPIs a serem escritos: ");
                            quantKPI = ler.nextInt();
                            System.out.println();
                        } while(quantKPI <= 0);
                        try { 
                            // Quantidade de Provedores
                            do {
                                System.out.print("Quantidade de provedores a serem escritos: ");
                                quantProv = ler.nextInt();
                                System.out.println();
                            } while(quantProv <= 0);
                            // Nomes padrões para os provedores
                            dados = "P1";
                            for(i = 1; i < quantProv; i++)                        
                                dados += ", P" + (i+1);                            
                            bw.append(dados);
                            bw.newLine();
                            // Nomes padrões para os KPIs + custo
                            dados = "kpi 1";
                            for(i = 1; i < quantKPI; i++)                        
                                dados += ", kpi " + (i+1);                            
                            bw.append(dados + ", custo");
                            bw.newLine();
                            // Tipos aleatórios para os KPIs + tipo custo (LB sempre)
                            dados = geraRandomTipo();
                            for(i = 1; i < quantKPI; i++)                        
                                dados += ", " + geraRandomTipo();                            
                            bw.append(dados + ", LB");
                            bw.newLine();
                            // Valores aleatórios para os KPIs                            
                            for(i = 0; i < quantKPI; i++)
                            {                                
                                if(geraRandomBool()) // KPI discreto
                                {
                                    dados = String.valueOf(geraRandomInt(MIN, MAX));
                                    for(j = 1; j < quantProv; j++)
                                        dados += "\t" + geraRandomInt(MIN, MAX);
                                    bw.append(dados);
                                    bw.newLine();
                                }
                                else // KPI contínuo
                                {
                                    dados = String.valueOf(arredonda(geraRandomReal(MIN, MAX), 2));
                                    for(j = 1; j < quantProv; j++)
                                        dados += "\t" + arredonda(geraRandomReal(MIN, MAX), 2);
                                    bw.append(dados);
                                    bw.newLine();
                                }
                            }                            
                            // Valores aleatórios para o custo dos provedores
                            dados = String.valueOf(arredonda(geraRandomReal(CUSTO_MIN, CUSTO_MAX), 2));
                            for(i = 1; i < quantProv; i++)
                                dados += "\t" + arredonda(geraRandomReal(CUSTO_MIN, CUSTO_MAX), 2);
                            bw.append(dados); 
                            bw.close();
                            System.out.println("\n# # # # FINALIZADO! # # # #\n");
                        }
                        catch(IOException ex)
                        {
                            System.out.println("Erro com o arquivo: " + ex.getMessage());                             
                        }                        
                        break;
                    default:
                        System.out.println("O valor informado é inválido!");   
                }                
            }
            catch(InputMismatchException ex)
            {
                System.err.println("Valor informado inválido: " + ex.getMessage());
            }
            catch(NoSuchElementException ex)
            {
                System.err.println("Valor não encontrado: " + ex.getMessage());  
            }
        }  
    }
    
    /************************** FUNÇÕES AUXILIARES *****************************/
    /**
     * Função de arredondamento automático.
     * @param numero Número a ser arredondado.
     * @param casaDecimal A casa decimal ser arredondada.
     * @return O Número arredondado.
     */
    public static double arredonda(double numero, int casaDecimal)
    {
        if(numero == 0.0 || casaDecimal < 0)
            return 0.0;
        if(casaDecimal == 0)
            return Math.round(numero); 
        // casaDecimal > 0
        numero = numero * Math.pow(10, casaDecimal);        
        numero = Math.round(numero);        
        return (numero / Math.pow(10, casaDecimal));
    }
    /**
     * Gera um número aleatorio inteiro entre os limites passados por parâmetro (min e max).
     * @param min Valor do limite inferior mínimo do número.
     * @param max Valor do limite superior máximo do número.
     * @return Um inteiro aleatório entre [min, max].
     */
    private static int geraRandomInt(int min, int max)
    {
        Random gerador = new Random();
        
        return min + gerador.nextInt(max + 1);
    }
    /**
     * Gera um número aleatorio em ponto flutuante (double) entre os limites passados 
     * por parâmetro (min e max).
     * @param min Valor do limite inferior mínimo do número.
     * @param max Valor do limite superior máximo do número.
     * @return Um double aleatório entre [min, max).
     */
    private static double geraRandomReal(double min, double max)
    {
        Random gerador = new Random();
        
        return min + (max - min) * gerador.nextDouble();
    } 
    /**
     * Gera um booleano aleatório.
     * @return Um booleano aleatório qualquer.
     */
    private static boolean geraRandomBool()
    {
        Random gerador = new Random();
        
        return gerador.nextBoolean();
    }
    /**
     * Gera um tipo aleatório para um KPI: nb, hb ou lb.
     * @return String "NB" ou "HB" ou "LB". 
     */
    private static String geraRandomTipo()
    {
        int tipo = geraRandomInt(0, 1);
        
        if(tipo == 0)
            return "HB";
        else if(tipo == 1)
            return "LB";
        else
            return "NB";       
    }
}

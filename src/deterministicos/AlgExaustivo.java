/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package deterministicos;

import aplicacao.AvaliaFitness;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import modelosMetaheuristicas.IndividuoBin;
import java.util.ArrayList;
import static modelos.Constantes.*;

public class AlgExaustivo 
{    
    /** A quantidade de provedores na base de dados. */
    private int NUM_PROV;    
    /** O número identificador da requisiçao. */
    private int NUM_REQ;    
    /** A quantidade de PIs na base de dados. */
    private int NUM_PIS;
    /** O número identificador da base de dados utilizada. */
    private int NUM_DB;
    /**
     * Implementa o algoritmo exaustivo de busca de fitness. Possui complexidade
     * exponencial ao numero de provedores da base de dados (2^n).
     * @param NUM_PROV O numero total de provedores da base de dados.
     * @param NUM_REQ O numero identificador da requisiçao do cliente.
     * @param NUM_PIS O numero total de PIs na base de dados.
     * @param NUM_DB O numero identificador da base de dados utilizada.
     */
    public AlgExaustivo(int NUM_PROV, int NUM_REQ, int NUM_PIS, int NUM_DB)
    {
        if(NUM_PROV <= 0 || NUM_REQ <= 0 || NUM_PIS <= 0 || NUM_DB <= 0)
            throw new IllegalArgumentException("AlgExaustivo: Todos os parametros devem ser maiores que zero!");
        this.NUM_PROV = NUM_PROV;
        this.NUM_REQ = NUM_REQ;
        this.NUM_PIS = NUM_PIS;
        this.NUM_DB = NUM_DB;
    }
    // GETTERS
    /** @return O numero total de provedores da base de dados. */
    public int getNUM_PROV() 
    {
        return NUM_PROV;
    }
    /** @return O numero identificador da requisiçao do cliente. */
    public int getNUM_REQ() 
    {
        return NUM_REQ;
    }
    /** @return O numero total de PIs na base de dados. */
    public int getNUM_PIS()
    {
        return NUM_PIS;
    }
    /** @return O numero identificador da base de dados utilizada. */
    public int getNUM_DB() 
    {
        return NUM_DB;
    }
    // SETTERS
    /**
     * Modifica o identificador da quantidade de provedores na base de dados.
     * @param NUM_PROV O novo valor do identificador da quantidade de provedores na base de dados.
     */
    public void setNUM_PROV(int NUM_PROV) 
    {
        if(NUM_PROV > 0)
            this.NUM_PROV = NUM_PROV;
    }
    /**
     * Modifica o identificador da requisiçao do cliente.
     * @param NUM_REQ O novo valor do identificador da requisiçao do cliente.
     */
    public void setNUM_REQ(int NUM_REQ) 
    {
        if(NUM_REQ > 0)
            this.NUM_REQ = NUM_REQ;
    }
    /**
     * Modifica o identificador da quantidade de PIs da base de dados.
     * @param NUM_PIS O novo valor do identificador da quantidade de PIs da base de dados.
     */
    public void setNUM_PIS(int NUM_PIS) 
    {
        if(NUM_PIS > 0)
            this.NUM_PIS = NUM_PIS;
    }
    /**
     * Modifica o identificador da base de dados utilizada.
     * @param NUM_DB O novo valor do identificador da base de dados utilizada.
     */
    public void setNUM_DB(int NUM_DB) 
    {
        if(NUM_DB > 0)
            this.NUM_DB = NUM_DB;
    } 
    
    /**
     * Roda o Algoritmo exaustivo de avaliaçao e busca do(s) melhor(es) fitness. 
     * Exponencial a quantidade de provedores, "NUM_VARS".
     * @return O(s) melhor(es) individuo(s) encontrado(s) pelo algoritmo exaustivo.
     */
    public ArrayList<IndividuoBin> rodaExaustivo()
    {              
        // Carrega os dados
        String ArqBD = "Dados/Base" + NUM_DB + "/DADOS" + NUM_PROV + "x" + NUM_PIS + ".txt";
        String ArqReq = "Requisicoes/Base" + NUM_DB + "/REQ" + NUM_REQ + "_" + NUM_PIS + ".txt";
        AvaliaFitness af = new AvaliaFitness(ArqBD, ArqReq);
        //af.imprime();
        
        // Variaveis
        int i, NUM_VARS = af.getBase_dados().getNumProvedores();
        long num = 1, numFinal = 0;
        long tempoInicial, tempoFinal;
        double fitMelhor = 0.00;
        ArrayList<IndividuoBin> respostas = new ArrayList();
        IndividuoBin atual;
        String bin;        
       
        System.out.println("# # # # # Algoritmo de Busca Exaustiva # # # # #\n");
        //System.out.println("Espaço de busca: " + (long)(Math.pow(2, NUM_VARS) - 1) );
        System.out.println("NumProv: " + NUM_PROV + "\nNumReq: " + NUM_REQ + "\nNumPIs: " + NUM_PIS + "\nNumDB: " + NUM_DB);
        
        for(i = NUM_VARS - 1; i >= 0; i--)
            numFinal += (long)Math.pow(2, i);
        
        //System.out.println("Iterações com " + NUM_VARS + " variaveis: " + numFinal);
        // Inicia o cronômetro
        tempoInicial = System.currentTimeMillis();        
        while(num <= numFinal)
        {
            atual = new IndividuoBin(NUM_VARS);
            bin = converteDecimalParaBinario(num);            
            atual.setCodificacao(stringBooleanos(bin, NUM_VARS));
            //System.out.println("Binário (" + num + "): " + bin);
            //atual.imprimeCodificacao();
            af.fitness(atual);
            if(atual.getFitness() == fitMelhor)
                respostas.add(atual);
            else if(atual.getFitness() > fitMelhor)
            {
                respostas.clear();
                fitMelhor = atual.getFitness();
                respostas.add(atual);
            }
            num++;
        }
        // Interrompe o cronômetro
        tempoFinal = (System.currentTimeMillis() - tempoInicial);
        System.out.println("Tempo de execução: " + tempoFinal + " ms ou " + (tempoFinal/1000.0) + " seg\n");
        // Resposta
        if(!respostas.isEmpty())
        {
            System.out.println("Resposta(s):");
            for(i = 0; i < respostas.size(); i++)
            {
                System.out.println("Melhor fitness: " + respostas.get(i).getFitness());
                //System.out.print("Codificação: ");
                //respostas.get(i).imprimeCod();
                //respostas.get(i).imprimeProvCod();
            }
        }
        else
        {
            System.out.println("Não há solução para essa requisição.");
        }
        // Guarda em arquivo a resposta
        try {
            FileWriter fw = new FileWriter("EstatisticasExaustivo.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);                        
            // Escreve: Numero da requisicao (Num prov)(Num pis) <TAB> TEMPO (ms) <TAB> Melhor fitness
            bw.append("(Req " + NUM_REQ + ")(Provs " + NUM_PROV + ")(PIs " + NUM_PIS + ")\t" + tempoFinal + " ms" + "\t" + respostas.get(0).getFitness());
            bw.newLine();
            // Escreve: Resposta 1 <ENTER> Resposta 2 <ENTER> ...
            for(i = 0; i < respostas.size(); i++)
            {
                bw.append(respostas.get(i).provCod());
                bw.newLine();
            }
            //bw.newLine();
            bw.close();
        }
        catch(IOException ex)
        {
            System.out.println("Erro ao escrever no arquivo de estatisticas do Exautivo: " + ex.getMessage());
        }
        return respostas;
    }
    /**
     * Retorna uma cadeia de booleanos representando o número binário na String 
     * passada, com a quantidade de bits passada.
     * @param bin Número binário em String.
     * @param tamanho Quantidade de bit do número binário.
     * @return Vetor de booleanos representendo o número binário passado.
     */
    private static boolean[] stringBooleanos(String bin, int tamanho)
    {
        boolean[] retorno = new boolean[tamanho];
        
        if(bin.length() > tamanho || tamanho <= 0)
            throw new IllegalArgumentException("Binário e/ou tamanho incompatível");
        
        while(bin.length() < tamanho)
            bin = "0" + bin;
        
        for(int i = 0; i < bin.length(); i++)
        {
            if(bin.charAt(i) == '0')
                retorno[i] = false;
            else if(bin.charAt(i) == '1')
                retorno[i] = true;
            else
                throw new IllegalArgumentException("O número passado não é um binário");
        }        
        return retorno;
    }
    
    /**
     * Converte decimal para binário. A regra é ficar dividindo o valor por 2,
     * pegar o resto de cada divisão e inserir o valor da direita para a esquerda 
     * na String de retorno. O algoritmo é executado até que o valor que foi
     * sucessivamente dividido se torne 0. 
     * Exemplo: 13
     * 13/2 = 6 -> resto 1 -> Resultado: 1
     * 6/2 = 3  -> resto 0 -> Resultado: 01
     * 3/2 = 1  -> resto 1 -> Resultado: 101
     * 1/2 = 0  -> resto 1 -> Resultado: 1101
     * Resultado: 1101
     * @param valor Número decimal positivo a ser convertido.
     * @return String contendo o valor em binário.
     */
    private static String converteDecimalParaBinario(long valor) 
    {       
       if(valor <= 0) 
          return "0";
       
       long resto;
       StringBuilder sb = new StringBuilder();
       // Enquanto o resultado da divisão por 2 for maior que 0 adiciona o resto ao início da String de retorno
       while(valor > 0) 
       {
          resto = valor % 2;
          valor = valor / 2;
          sb.insert(0, resto);
       }
       return sb.toString();
    }
}

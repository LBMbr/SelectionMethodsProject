/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package aplicacao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
//import static modelos.Constantes.*;

public abstract class ReplicadorBD 
{
    private static final int NUM_REPLICAS = 3;
    private static final String ARQ_PATH = "Dados/Base6/";
    private static final String ARQ_BASE = "DADOS10x4.txt";
    
    public static void main(String args[])
    {
        FileReader fr;      BufferedReader br;
        FileWriter fw;      BufferedWriter bw;
        String linha;
        String[] nomesPis, valores;        
        int i, j, numProv, numPIs;
        try { 
            // # # # Arquivo para leitura # # #
            fr = new FileReader(ARQ_PATH + ARQ_BASE);
            br = new BufferedReader(fr);
            // # # # # # PRIMEIRA LINHA: (Quantidade de Provedores) OU (Nome dos Provedores) # # # # #
            linha = br.readLine();
            try {   // Quantidade de provedores?
                numProv = Integer.parseInt(linha);                
            }
            catch(NumberFormatException ex)
            {   // Não, nome dos provedores entao ...                
                // Torna a String minúscula e remove qualquer espaçamento nela
                linha = linha.toLowerCase().replaceAll(" ", "").replaceAll("\t", "");  
                // Quantidade de provedores
                numProv = (linha.split(",")).length;                
            }
            if(numProv <= 0)
                throw new IllegalArgumentException("Nao ha provedores suficientes informados!\n");
            
            // # # # # # SEGUNDA LINHA: Nome dos PIs [Grupo dos PIs] # # # # #
            linha = br.readLine();            
            //linha = linha.toLowerCase().replaceAll(" ", "").replaceAll("\t", "");  
            // Separa os nomes dos PIs
            nomesPis = linha.split(",");
            numPIs = nomesPis.length;
            if(numPIs <= 0)
                throw new IllegalArgumentException("Nao ha PIs suficientes informados!\n");
            
            // # # # Arquivo para escrita # # #
            fw = new FileWriter(ARQ_PATH + "DADOS" + (numProv * NUM_REPLICAS) + "x" + (numPIs - 1) + ".txt");            
            bw = new BufferedWriter(fw);
            // Escreve a nova quantidade de provedores
            bw.append("" + (numProv * NUM_REPLICAS));
            bw.newLine();
            // Copia a linha dos PIs
            bw.append(linha);
            bw.newLine(); 
            // Acha a posicao do PI custo
            /*posCusto = - 1;
            for(i = 0; i < numPIs; i++)
            {        
                // NomePI[GrupoPI]
                if(nomesPis[i].contains("[") && nomesPis[i].contains("]"))                
                    nome = nomesPis[i].substring(0, nomesPis[i].indexOf('['));                                        
                // NomePI -> sem grupo
                else
                    nome = nomesPis[i];
                
                if(nome.equals("custo"))
                    posCusto = i;
            }
            if(posCusto == -1)
                throw new IllegalArgumentException("O custo nao foi encontrado!\n");*/
            
            // # # # # # TERCEIRA LINHA: Tipos dos PIs # # # # #
            linha = br.readLine();
            // Verifica a consistencia da entrada
            if(nomesPis.length != linha.split(",").length)
                throw new IllegalArgumentException("A quantidade de PIs informados ("
                    + nomesPis.length + ") difere da quantidade de tipos de PIs "
                    + "informados (" + linha.split(",").length + ")!\n");
            // Copia a linha dos tipos dos PIs
            bw.append(linha);
            bw.newLine(); 
            
            // # # # # # DEMAIS LINHAS: Valores dos PIs # # # # # 
            i = 0; // Conta linhas restantes            
            while((linha = br.readLine()) != null)
            { 
                linha = linha.replaceAll(" ", "");
                valores = linha.split("\t");
                if(valores.length != numProv)
                    throw new IllegalArgumentException("Quantidade de valores informada ("
                        + valores.length + ") ao PI " + (i+1) + ", difere da "
                        + "quantidade de provedores passada (" + numProv + ")!\n");
                i++;            
                for(j = 0; j < NUM_REPLICAS; j++)
                    bw.append(linha + "\t");
                bw.newLine();                               
            } 
            br.close(); 
            bw.close();
            System.out.println("Arquivo: " + ARQ_BASE + " replicado " + NUM_REPLICAS + " vezes!\n");
            System.out.println("# # # FINALIZADO! # # #");
        }
        catch (IOException ex)
        {
            System.err.println("ReplicadorBD: Erro com os arquivos: " + ex.getMessage());
        }
    }    
}

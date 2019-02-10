/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package modelosMetaheuristicas;

import java.util.Random;

public abstract class OperadorSelecao 
{
    /** Gerador randomico da classe. */
    private static final Random gerador = new Random();
    
/************************** SELEÇÃO POR ROLETA ****************************/
    /**
     * Método da roleta para uma população binária no AG.
     * @param P Uma população binária com um número par de individuos.
     * @return Uma população de mesmo tamanho de "P", resultante do processo de seleção.
     */
    public static PopulacaoBin roleta(PopulacaoBin P)
    {
        int i, j, indJaEscolhido;        
        double[] roleta = new double[P.getTamPop()];        
        double sorteio, soma, fitJaEscolhido, fitTotal;        
        PopulacaoBin populacao_final = new PopulacaoBin(P.getTamPop(), P.getNumVars());          
        
        if(P.getTamPop() < 2 || P.getTamPop()% 2 != 0)        
            throw new IllegalArgumentException("O tamanho da população deve ser par e positiva.");            
        
        // Calculo do fitness total da população
        fitTotal = 0.00;        
        for(i = 0; i < P.getTamPop(); i++)        
            fitTotal += P.getIndPos(i).getFitness();    
        //System.out.println("\nFitness total: " + fitTotal);
        // Calculo do fitness relativo e atribuição do mesmo a roleta
        for(i = 0; i < P.getTamPop(); i++)       
            roleta[i] = (P.getIndPos(i).getFitness() / fitTotal); 
        
        // Sorteio dos tamPop/2 pares 
        for(i = 0; i < P.getTamPop(); i = i + 2)
        {
            //System.out.println("Par " + (i+2)/2 + ":");
            // # # # Sorteio do PRIMEIRO individuo do par # # #
            sorteio = geraRandomReal(0.00, 1.00); // vai de 0 até 1
            /*for(j = 0; j < roleta.length; j++)            
                System.out.print(roleta[j] + "  "); // DEBUGS            
            System.out.println("\nSorteio: " + sorteio);*/
            j = 0;
            soma = 0.00;
            while(sorteio >= soma)
            {
                soma = soma + roleta[j];
                j++;
                // Condicao de seguranca, caso extrapole o tamanho da população
                if(j > P.getTamPop())  j = 1;                
            }
            //System.out.println("Individuo 1: " + (j) + "\n");
            populacao_final.setIndPos(P.getIndPos(j - 1), i); // Grava o individuo sorteado na posição i           
            indJaEscolhido = j - 1; // Grava o indice ja escolhidos
            fitJaEscolhido = roleta[indJaEscolhido]; // Grava o fitness sorteado
            roleta[indJaEscolhido] = 0.00; // Apaga (zera) o valor do fitness do "IndJaEscolhido" da roleta
            
            // # # # Sorteio do SEGUNDO individuo do par # # #
            sorteio = geraRandomReal(0.00, 1.00 - fitJaEscolhido); // vai de 0 até (1 - fitJaEscolhido) 
            /*for(j = 0; j < roleta.length; j++)            
                System.out.print(roleta[j] + "  "); // DEBUGS            
            System.out.println("\nSorteio: " + sorteio + "  Max = " + (fitTotal - fitJaEscolhido));*/
            j = 0;
            soma = 0.00;
            while(sorteio >= soma)
            {                
                soma = soma + roleta[j];
                j++;
                // Condicao de seguranca, caso extrapole o tamanho da população
                if(j > P.getTamPop())  j = 1;                
            }
            //System.out.println("IndividuoBin 2: " + (j)+ "\n");
            populacao_final.setIndPos(P.getIndPos(j - 1), i + 1); // Grava o individuo sorteado na posição i+1 
            roleta[indJaEscolhido] = fitJaEscolhido; // Coloca novamente o fitness zerado na roleta
        }        
        return populacao_final;
    }    
    /************************* SELEÇÃO POR TORNEIO ****************************/
    /**
     * Método do torneio estocástico para uma população binária no AG.
     * @param P Uma população binária, deve ter numero par de individuos.
     * @param k O tamanho do torneio, deve ser maior que zero.
     * @return Uma população de mesmo tamanho de "P", resultante do processo de seleção.
     */
    public static PopulacaoBin torneio(PopulacaoBin P, int k)
    {       
        int i, j, sorteio, idMelhor;
        double fitMelhor;        
        boolean[] flags = new boolean[P.getTamPop()];
        PopulacaoBin populacao_final = new PopulacaoBin(P.getTamPop(), P.getNumVars());
        //Random gerador = new Random();        
        
        if(P.getTamPop() < 2 || P.getTamPop()%2 != 0 || k <= 0 || k > P.getTamPop())        
            throw new IllegalArgumentException("O tamanho da população deve ser par, positiva e com k entre 0 e o tamanho da população.");            
                
        // Inicio do torneio
        for(i = 0; i < P.getTamPop(); i = i + 2)
        { 
            //System.out.println("Par " + (i+2)/2 + ":");
            // # # # Escolha do PRIMEIRO INDIVIDUO DO PAR # # #
            // Zera o vetor de flags para não repetir posições
            for(j = 0; j < P.getTamPop(); j++)            
                flags[j] = false; 
            // Inicia variaveis
            idMelhor = -1;
            fitMelhor = 0.00;
            // DEBUG             
            //for(j = 0; j < P.getTamPop(); j++)            
                //System.out.print(P.getIndividuos()[j].getFitness() + "  ");            
            //System.out.println();
            // Inicio do torneio  
            for(j = 0; j < k; j++)
            {
                do {                    
                    sorteio = gerador.nextInt(P.getTamPop()); // Gera indices: 0 a (tamPop - 1)
                    //System.out.println("Sorteio " + (j+1) + " = " + (sorteio+1) + " (" + P.getIndividuos()[sorteio].getFitness() + ")");
                } while(flags[sorteio] == true);
                flags[sorteio] = true;
                if(P.getIndPos(sorteio).getFitness() >= fitMelhor)
                {
                    fitMelhor = P.getIndPos(sorteio).getFitness();
                    idMelhor = sorteio;
                }                
            }  
            //System.out.println("Indice do campeão: " + (idMelhor+1) + " (" + fitMelhor + ")");
            // Coloca uma cópia do melhor indivíduo sorteado na população final  
            populacao_final.setIndPos(P.getIndPos(idMelhor), i);                        
                      
            // # # # Escolha do SEGUNDO INDIVIDUO DO PAR # # #
            // Zera o vetor de flags para não repetir posições
            for(j = 0; j < P.getTamPop(); j++)            
                flags[j] = false;        
            // IndividuoBin de "idMelhor" já foi sorteado
            flags[idMelhor] = true; 
            // Reseta variaveis
            idMelhor = -1;
            fitMelhor = 0.00;
            // DEBUG   
            //for(j = 0; j < P.getTamPop(); j++)            
                //System.out.print(P.getIndividuos()[j].getFitness() + "  ");            
            //System.out.println();
            // Escolha do SEGUNDO INDIVIDUO DO PAR
            for(j = 0; j < k; j++)
            {
                do {                    
                    sorteio = gerador.nextInt(P.getTamPop()); // Gera indices: 0 -> P.getNumInd()-1
                    //System.out.println("Sorteio " + (j+1) + " = " + (sorteio+1) + " (" + P.getIndividuos()[sorteio].getFitness() + ")");
                } while(flags[sorteio] == true);
                flags[sorteio] = true;
                if(P.getIndPos(sorteio).getFitness() >= fitMelhor)
                {
                    fitMelhor = P.getIndPos(sorteio).getFitness();
                    idMelhor = sorteio;
                }
            }
            //System.out.println("Indice do campeão: " + (idMelhor+1) + " (" + fitMelhor + ")");
            // Coloca uma cópia do melhor indivíduo sorteado na população final  
            populacao_final.setIndPos(P.getIndPos(idMelhor), i + 1);   
        }
	return populacao_final;
    }    
        
    /*************************** FUNÇÃO AUXILIAR ******************************/
    /**
     * Gera um número aleatorio em ponto flutuante (double) entre os limites passados 
     * por parâmetro (min e max).
     * @param min Valor do limite inferior mínimo do número.
     * @param max Valor do limite superior máximo do número.
     * @return Um double aleatório entre [min, max).
     */
    private static double geraRandomReal(double min, double max)
    {
        //Random gerador = new Random();        
        return (min + (max - min) * gerador.nextDouble());
    }
}
/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package modelosMetaheuristicas;

import java.util.Random;

public abstract class OperadorGenetico 
{
    /** Gerador randomico da classe. */
    private static final Random gerador = new Random();
    
/****************************** CROSSOVER **************************************/
    /**
     * Metodo de crossOver com um único ponto de corte para dois individuos binários,
     * suas codificações devem ter o mesmo tamanho.
     * @param pai1 Individuos binario primeiro pai.
     * @param pai2 IndividuoBin binario segundo pai.
     * @param PROB_CROSSOVER Probabilidade de ocorrer o crossOver, vai de 0 a 100%.
     * @return True se o crossOver ocorreu com sucesso, false caso contrario.
     */
    public static boolean onePointCrossOver(IndividuoBin pai1, IndividuoBin pai2, int PROB_CROSSOVER)
    {
        int prob, corte, numVars = pai1.getTamCod();         
        IndividuoBin filho1, filho2;        
        //Random gerador = new Random();
        
        /*if(pai1.getId() <= 0 || pai2.getId() <= 0)
            throw new IllegalArgumentException("Individuos inválidos para crossOver!");*/
        if(pai1.getTamCod() != pai2.getTamCod())        
            throw new IllegalArgumentException("Individuos com diferentes tamanhos de codificação não podem cruzar!");
        if(PROB_CROSSOVER < 0 || PROB_CROSSOVER > 100)
            throw new IllegalArgumentException("A probabilidade deve assumir um valor inteiro de 0 a 100!");
        
        // Verifica se ocorre crossOver
        prob = gerador.nextInt(100);
        if(prob < PROB_CROSSOVER)
        {
            filho1 = new IndividuoBin(numVars);  filho1.clone(pai1);
            filho2 = new IndividuoBin(numVars);  filho2.clone(pai2);  
            // Variável de corte que define o ponto de mudança
            corte = 1 + gerador.nextInt(numVars - 1);
            //System.out.println("Corte (ind " + pai1.getId() + "," + pai2.getId() + ") = " + corte);
            // Acontece o crossOver para cada par de variável
            for(int i = 0; i < numVars; i++)
            {                
                if(i < corte)
                {
                    filho1.setCodPos(i, pai1.getCodPos(i));
                    filho2.setCodPos(i, pai2.getCodPos(i));
                }
                else
                {
                    filho1.setCodPos(i, pai2.getCodPos(i));
                    filho2.setCodPos(i, pai1.getCodPos(i));                    
                } 
            }
            // Subtituição dos pais pelos filhos
            pai1.clone(filho1); 
            pai2.clone(filho2);   
            // Verifica se foi gerado algum individuo nulo - Todo com false
            if(pai1.individuoNulo())
                pai1.setCodPos(gerador.nextInt(pai1.getTamCod()), true); // Bitflip em uma posição aleatoria
            if(pai2.individuoNulo())
                pai2.setCodPos(gerador.nextInt(pai2.getTamCod()), true);
            
            return true;
        }
        return false;
    }    
    /**
     * Metodo de crossOver usando uma máscara binaria de decisão, para dois individuos
     * binários, suas codificações devem ter o mesmo tamanho. 
     * @param pai1 IndividuoBin binario primeiro pai.
     * @param pai2 IndividuoBin binario segundo pai.
     * @param PROB_CROSSOVER Probabilidade de ocorrer o crossOver, vai de 0 a 100%.
     * @return True se o crossOver ocorreu com sucesso, false caso contrario.
     */
    public static boolean binMaskCrossOver(IndividuoBin pai1, IndividuoBin pai2, int PROB_CROSSOVER)
    {
        int prob, numVars = pai1.getTamCod();         
        boolean decide;
        IndividuoBin filho1, filho2;
        //Random gerador = new Random();
        
        /*if(pai1.getId() <= 0 || pai2.getId() <= 0)
            throw new IllegalArgumentException("Individuos inválidos para crossOver!");*/
        if(pai1.getTamCod() != pai2.getTamCod())        
            throw new IllegalArgumentException("Individuos com diferentes tamanhos de codificação não podem cruzar!");
        if(PROB_CROSSOVER < 0 || PROB_CROSSOVER > 100)
            throw new IllegalArgumentException("A probabilidade deve assumir um valor inteiro de 0 a 100!");
        // Verifica se ocorre crossOver
        prob = gerador.nextInt(100);
        if(prob < PROB_CROSSOVER)
        {
            filho1 = new IndividuoBin(numVars);  filho1.clone(pai1);
            filho2 = new IndividuoBin(numVars);  filho2.clone(pai2);            
            // Acontece o crossOver para cada par de variável
            for(int i = 0; i < numVars; i++)
            {   
                decide = gerador.nextBoolean();
                //System.out.print("Mascara (ind " + pai1.getId() + "," + pai2.getId() + " | var " + (i+1) + "): " + decide);
                if(decide == true)
                {
                    filho1.setCodPos(i, pai1.getCodPos(i));
                    filho2.setCodPos(i, pai2.getCodPos(i));
                }
                else
                {
                    filho1.setCodPos(i, pai2.getCodPos(i));
                    filho2.setCodPos(i, pai1.getCodPos(i));                
                }                         
            }
            // Subtituição dos pais pelos filhos
            pai1.clone(filho1); 
            pai2.clone(filho2);   
            // Verifica se foi gerado algum individuo nulo - Todo com false
            if(pai1.individuoNulo())
                pai1.setCodPos(gerador.nextInt(pai1.getTamCod()), true); // Bitflip em uma posição aleatoria
            if(pai2.individuoNulo())
                pai2.setCodPos(gerador.nextInt(pai2.getTamCod()), true);
            
            return true;
        }
        return false;
    }

/********************************* MUTAÇÃO **************************************/
    /**
     * Operador de mutação simples para a população binária (bit flip).
     * @param ind Indivíduo que poderá ser mutado com probabilidade "PROB_MUTACAO".
     * @param PROB_MUTACAO Probabilidade de ocorrer a mutação, vai de 0 a 100%.
     * @return A quantidade de mutações que o individuo "ind" sofreu no processo.
     */
    public static int bitFlipMutation(IndividuoBin ind, int PROB_MUTACAO)
    {
        int prob, flag;
        //Random gerador = new Random();
        
        if(PROB_MUTACAO < 0 || PROB_MUTACAO > 100)
            throw new IllegalArgumentException("A probabilidade de mutação deve assumir um valor inteiro de 0 a 100!");
        
        if(ind != null)
        {
            flag = 0;            
            for(int i = 0; i < ind.getTamCod(); i++)
            {
                prob = gerador.nextInt(100);
                if(prob < PROB_MUTACAO)
                {   // Ocorreu uma Mutaçao no individuo ind
                    flag++;
                    ind.setCodPos(i, !ind.getCodPos(i));    
                }
            }
            // Verifica se foi gerado algum individuo nulo - Todo com false
            if(ind.individuoNulo())
                ind.setCodPos(gerador.nextInt(ind.getTamCod()), true); // Bitflip em uma posição aleatoria
           
            return flag;
        }
        return 0;
    }    
}

/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package modelosMetaheuristicas;

public abstract class EscalonamentoLinear 
{
    /**
     * Função responsável por aplicar o escalonamento linear no fitness de cada 
     * individuo membro da população "p", passada por parâmetro.
     * @param p A polulaçao que se deseja aplicar o escalonamento linear.
     * @param C Constante de escalonamento "C", cujo domínio varia entre [1.2, 2.0]. 
     * Representa a razão desejada entre o fitness escalonado do melhor indivíduo 
     * e o fitness escalonado médio da população.
     */
    public static void escalonamento(PopulacaoBin p, double C)
    {
        double alpha, beta, Fmin, Fmax, Favg;
        IndividuoBin ind;
        
        if(p == null || p.getTamPop() <= 0 || C < 1.2 || C > 2.0)
            throw new IllegalArgumentException("Escalonamento: Valores de entrada inválidos!");
        
        Fmin = p.getPiorInd().getFitness();
        Fmax = p.getMelhorInd().getFitness();
        Favg = p.getFitnessMedio();
        //System.out.println("C = " + C + ", Fmin = " + Fmin + ", Fmax = " + Fmax + ", Favg = " + Favg + "\n");
        
        if(Fmin > (C * Favg - Fmax)/(C - 1))
        {
            //System.out.println("Entrou em 1!\n");
            alpha = (Favg * (C - 1))/(Fmax - Favg);
            beta = (Favg * (Fmax - C * Favg))/(Fmax - Favg);
        }
        else
        {
            //System.out.println("Entrou em 2!\n");
            alpha = Favg/(Favg - Fmin);
            beta = (- Fmin * Favg)/(Favg - Fmin);
        }
        //System.out.println("Alpha = " + alpha + "\nBeta = " + beta + "\n");        
        
        for(int i = 0; i < p.getTamPop(); i++)
        {
            ind = p.getIndPos(i);
            ind.setFitness( alpha * (ind.getFitness()) + beta );
        }
    }
}

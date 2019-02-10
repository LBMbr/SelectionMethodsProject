/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package deterministicos;

public class Matching_DadosProvs
{
    /** Nome do provedor que possui esses dados. */
    private String nomeProv;
    /** Pontuação dada ao Provedor pelo método de seleção para cada nível de prioridade. */
    private final double[] scoreNivel; 
    /** Pontuação final dada ao Provedor pelo método de seleção. */
    private double scoreFinal = 0.00;
    
    /**
     * 
     * @param nomeProv
     * @param numNiveis 
     */
    public Matching_DadosProvs(String nomeProv, int numNiveis)
    {
        if(nomeProv == null || nomeProv.isEmpty())
            throw new IllegalArgumentException("O nome do provedor deve ser válido!");
        this.nomeProv = nomeProv;
        if(numNiveis < 0)
            throw new IllegalArgumentException("Quantidade de niveis invalida!");
        scoreNivel = new double[numNiveis];
        for(int i = 0; i < numNiveis; i++)
            scoreNivel[i] = 0.0;
    }
    
    // GETTERS
    /** @return O nome do provedor. */
    public String getNomeProv() 
    {
        return this.nomeProv;
    }
    /**   
     * @param pos A posicao desejada.
     * @return O score desse provedor na posição passada.
     */
    public double getScoreNivel(int pos) 
    {
        return scoreNivel[pos];
    }
    /** @return O score final do provedor. */
    public double getScoreFinal()
    {
        return this.scoreFinal;
    }
    
    // SETTERS
    /**
     * Modifica o nome do provedor, não pode ser vazio ou nulo.
     * @param nomeProv Novo nome do provedor.
     */
    public void setNome(String nomeProv)
    {
        if(nomeProv != null && ! nomeProv.isEmpty())
            this.nomeProv = nomeProv.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
    }
    /**
     * Muda o valor do score na posicao passada.
     * @param pos A posicao desejada.
     * @param novoScore O novo valor do score.
     */
    public void setScoreNivel(int pos, double novoScore)
    {
        this.scoreNivel[pos] = novoScore;
    }
    /**
     * Modifica o score final do provedor.
     * @param score Novo score.
     */
    public void setScoreFinal(double score)
    {
        this.scoreFinal = score;
    }
    
    // Adições de score
    /**
     * Adiciona ao valor do score na posicao passada com a quantidade passada.
     * @param pos A posicao.
     * @param score A quantidade.
     */
    public void addScoreNivel(int pos, double score) 
    {
        this.scoreNivel[pos] += score;
    }
    /**
     * Adiciona ao valor do score final a quantidade passada.
     * @param score Novo score.
     */
    public void addScoreFinal(double score)
    {
        this.scoreFinal += score;
    }
    
    @Override
    public String toString()
    {
        int i;
        String ret = "Provedor " + nomeProv + ":\nScore por niveis = ";
        for(i = 0; i < scoreNivel.length - 1; i++)
            ret += scoreNivel[i] + ", ";
        ret += scoreNivel[i] + "\nScore final = " + scoreFinal;
        return ret;
    }
}

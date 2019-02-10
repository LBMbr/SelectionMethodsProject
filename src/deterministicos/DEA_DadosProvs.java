/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package deterministicos;

public class DEA_DadosProvs 
{
    /** Nome do provedor que possui esses dados. */
    private String nomeProv;
    /** Valor do INPUT "recursos" deste provedor. */
    private double recursos = 0.00;
    /** Valor do INPUT "custos" deste provedor. */
    private double custos = 0.00;
    /** Valor do INPUT "adequacao" deste provedor. */
    private double adequacao = 0.00;
    /** Valor do INPUT "sobras" deste provedor. */
    private double sobras = 0.00;
    
    // # # # # # # # # Construtores # # # # # # # #
    
    public DEA_DadosProvs(String nomeProv)
    {
        if(nomeProv == null || nomeProv.isEmpty())
            throw new IllegalArgumentException("O nome do provedor deve ser válido!");
        this.nomeProv = nomeProv;
    }
    
    public DEA_DadosProvs(String nomeProv, double recursos, double custos, double adequacao, double sobras)
    {
        if(nomeProv == null || nomeProv.isEmpty())
            throw new IllegalArgumentException("O nome do provedor deve ser válido!");
        this.nomeProv = nomeProv;
        if(recursos < 0)
            throw new IllegalArgumentException("O valor do input recursos deve ser maior que zero!");
        this.recursos = recursos;
        if(custos < 0)
            throw new IllegalArgumentException("O valor do input custos deve ser maior que zero!");
        this.custos = custos;
        if(adequacao < 0)
            throw new IllegalArgumentException("O valor do output adequacao deve ser maior que zero!");
        this.adequacao = adequacao;
        if(sobras < 0)
            throw new IllegalArgumentException("O valor do output sobras deve ser maior que zero!");
        this.sobras = sobras;
    }
    
    // GETTERS
    /** @return O nome do provedor. */
    public String getNomeProv()
    {
        return this.nomeProv;
    }
    /** @return O valor do INPUT "recursos" para esse provedor. */
    public double getRecursos()
    {
        return recursos;
    }
    /** @return O valor do INPUT "custos" para esse provedor. */
    public double getCustos()
    {
        return custos;
    }
    /** @return O valor do OUTPUT "adequacao" para esse provedor. */
    public double getAdequacao()
    {
        return adequacao;
    }
    /** @return O valor do OUTPUT "sobras" para esse provedor. */
    public double getSobras()
    {
        return sobras;
    } 
    
    // SETTERS
    /**
     * Modifica o nome do provedor, não pode ser vazio ou nulo.
     * @param nomeProv Novo nome do provedor.
     */
    public void setNomeProv(String nomeProv)
    {
        if(nomeProv != null && ! nomeProv.isEmpty())
            this.nomeProv = nomeProv.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
    }
    /**
     * Modifica o valor do INPUT "recursos" deste provedor. Sempre positivo.
     * @param recursos Novo valor de "recursos".
     */
    public void setRecursos(double recursos)
    {
        if(recursos >= 0)
            this.recursos = recursos;
    }
    /**
     * Modifica o valor do INPUT "custos" deste provedor. Sempre positivo.
     * @param custos Novo valor de "custos".
     */
    public void setCustos(double custos)
    {
        if(custos >= 0)
            this.custos = custos;
    }
    /**
     * Modifica o valor do OUTPUT "adequacao" deste provedor. Sempre positivo.
     * @param adequacao Novo valor de "adequacao".
     */
    public void setAdequacao(double adequacao)
    {
        if(adequacao >= 0)
            this.adequacao = adequacao;
    }
    /**
     * Modifica o valor do OUTPUT "sobras" deste provedor. Sempre positivo.
     * @param sobras Novo valor de "sobras".
     */
    public void setSobras(double sobras)
    {
        if(sobras >= 0)
            this.sobras = sobras;
    }

    @Override
    public String toString()
    {
        return "Provedor " + nomeProv + ":\nRecursos = " + recursos + "\nCustos = "
                + custos + "\nAdequacao = " + adequacao + "\nSobras = " + sobras;
    }
}

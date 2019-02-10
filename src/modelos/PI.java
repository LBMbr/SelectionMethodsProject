/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package modelos;

import java.util.Objects;
import static modelos.Constantes.*;

public abstract class PI 
{
    /** Nome identificador (todo com letras minusculas e sem espaçamentos) do PI utilizado. */
    protected String nome = "";
    /** Tipo do indicador: NUMERICO, CAT_SIMP ou CAT_COMP. */
    protected int TYPE = VAZIO;
    /** Valor do peso (número real maior que zero) associado ao PI na requisição. */
    protected double peso = 0.0;
    /** Identificador do grupo de PIs a que esse PI pertence, zero caso não pertence a grupo algum. */
    protected int grupo = 0;
    /** Idica se esse PI eh essencial ao usuario, ou seja, deve ser atendido obrigatoriamente. */
    protected boolean essencial = false;
    
    // GETTERS
    /** @return O nome do PI. */
    public String getNome() 
    {
        return nome;
    }
    /** @return O tipo do PI: Numérico ou booleano ou categorigo simples, composto ou ordenado. */
    public int getTYPE()
    {
        return TYPE;
    }
    /** @return O peso do PI. */
    public double getPeso() 
    {
        return peso;
    }
    /** @return O grupo do PI. */
    public int getGrupo() 
    {
        return grupo;
    }
    /** @return O indicador que marca esse PI como essencial ou nao. */
    public boolean isEssencial()
    {
        return essencial;
    }
    /** @return O tipo de comportamento desse PI. */
    public int getTipo()
    {
        switch(TYPE)
        {
            case NUMERICO:
                PI_Num pi_num = (PI_Num) this;
                return pi_num.getTipo();
            case CAT_SIMP:
                PI_Cat pi_cat = (PI_Cat) this;
                return pi_cat.getTipo();
            case CAT_COMP:
                return NB;
            default:
                throw new IllegalArgumentException("O tipo do PI " + nome + " é desconhecido!\n");
        }
    }
    /** @return O valor do PI. */
    public String getValor()
    {
        switch(TYPE)
        {
            case NUMERICO:
                PI_Num pi_num = (PI_Num) this;
                return pi_num.getValor();
            case CAT_SIMP:
                PI_Cat pi_cat = (PI_Cat) this;
                return pi_cat.getValor();
            case CAT_COMP:
                PI_Comp pi_comp = (PI_Comp) this;
                return pi_comp.getValor();
            default:
                throw new IllegalArgumentException("O valor do PI " + nome + " é desconhecido!\n");
        }
    }
    /** @return A tolerancia desse PI, sempre ZERO se nao for um PI numerico. */
    public double getTol()
    {
        switch(TYPE)
        {
            case CAT_SIMP:
            case CAT_COMP:
                return 0.0;
            case NUMERICO:
                PI_Num pi_num = (PI_Num) this;
                return pi_num.getTol();
            default:
                throw new IllegalArgumentException("O tipo do PI " + nome + " é desconhecido!\n");
        }
    }
    /** @return A flag indicando se esse PI eh ordenado ou nao. Valido so para categorico simples. */
    public boolean isOrdenado()
    {
        switch(TYPE)
        {
            case CAT_SIMP:
                PI_Cat pi_cat = (PI_Cat) this;
                return pi_cat.isOrdenado();
        }
        return false;
    }
    // SETTERS
    /**
     * Modifica o nome do PI. Não deve ser vazio ou nulo.
     * @param nome Novo nome para o PI.
     */
    public void setNome(String nome)
    {
        if(nome != null && !nome.isEmpty())
            this.nome = nome.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
    }
    /**
     * Modifica o peso do PI.
     * @param peso Novo peso para o PI.
     */
    public void setPeso(double peso)
    {
        if(peso > 0)
            this.peso = peso;
    }
    /**
     * Modifica o grupo do PI.
     * @param grupo Novo grupo para o PI.
     */
    public void setGrupo(int grupo)
    {
        if(grupo >= 0)
           this.grupo = grupo;
    }
    /**
     * Modifica o indicador de essencialidade desse PI.
     * @param essencial Novo valor.
     */
    public void setEssencial(boolean essencial)
    {
        this.essencial = essencial;
    }
    /**
     * Modifica o valor da flag indicando se esse esse PI eh ordenado ou nao.
     * @param ordenado Nova flag indicando se eh ordenado ou nao.
     */
    public void setOrdenado(boolean ordenado)
    {
        switch(TYPE)
        {
            case CAT_SIMP:
                PI_Cat pi_cat = (PI_Cat) this;
                pi_cat.setOrdenado(ordenado);
                break;
        }
    }
    /**
     * Modifica o tipo do PI. Pode ser: NB, HB ou LB.
     * @param tipo Novo tipo para o PI.
     */
    public void setTipo(int tipo)
    {
        switch (TYPE) 
        {
            case NUMERICO:            
                PI_Num pi_num = (PI_Num) this;
                pi_num.setTipo(tipo);
                break;
            case CAT_SIMP:
                PI_Cat pi_cat = (PI_Cat) this;
                pi_cat.setTipo(tipo);
                break;
        }
    }
    /**
     * Modifica o valor do PI.
     * @param valor Novo valor para o PI.
     */
    public void setValor(String valor)
    {
        switch(TYPE)
        {
            case NUMERICO:
                PI_Num pi_num = (PI_Num) this;
                pi_num.setValor(Double.parseDouble(valor));
                break;
            case CAT_SIMP:
                PI_Cat pi_cat = (PI_Cat) this;
                pi_cat.setValor(valor);
                break;
            case CAT_COMP:
                PI_Comp pi_comp = (PI_Comp) this;
                pi_comp.setValor(valor.split(","));
                break;
        }
    }    
    /**
     * Modifica o valor da tolerancia do PI, sempre ZERO se nao for um PI numerico.
     * @param tol Novo valor de tolerancia (positivo) para o PI.
     */
    public void setTol(double tol)
    {
        switch(TYPE)
        {
            case NUMERICO:
                PI_Num pi_num = (PI_Num) this;
                pi_num.setTol(tol);
                break;
        }
    }     
    
    /**
     * Verifica se o tipo de PI passado é valido.
     * @param type O tipo de PI.
     * @return "true" caso o tipo seja valido; "false", caso contrario.
     */
    public static final boolean TYPEValido(int type)
    {
        return (type == NUMERICO || type == CAT_SIMP || type == CAT_COMP);
    }
    /**
     * Insere novas categorias nesse PI. So valido para categoricos.
     * @param cat As novas categorias.
     * @return "true" se conseguiu inserir, "false", caso contrario.
     */
    public boolean insereCategorias(Categoria[] cat)
    {
        if(cat != null)
        {
            switch(TYPE)
            {
                case CAT_SIMP:
                    PI_Cat pi_cat = (PI_Cat) this;
                    return pi_cat.insereNovasCategorias(cat);
                case CAT_COMP:
                    PI_Comp pi_comp = (PI_Comp) this;
                    return pi_comp.insereNovasCategorias(cat);
            }
        }
        return false;
    }
    /**
     * Obtem as categorias desse PI. So valido para categoricos.
     * @return As categorias desse PI.
     */
    public Categoria[] getCats()
    {
        switch(TYPE)
        {
            case CAT_SIMP:
                PI_Cat pi_cat = (PI_Cat) this;
                return pi_cat.getCats();
            case CAT_COMP:
                PI_Comp pi_comp = (PI_Comp) this;
                return pi_comp.getCats();
        }
        return null;
    }
    /** Imprime todas as categorias desse PI. So valido para categoricos. */
    public void imprimeCats()
    {
        switch(TYPE)
        {
            case CAT_SIMP:
                PI_Cat pi_cat = (PI_Cat) this;
                pi_cat.imprimeCats();
            case CAT_COMP:
                PI_Comp pi_comp = (PI_Comp) this;
                pi_comp.imprimeCats();
        }
    }
    /**
     * Verifica se esse PI atende um valor "y", desejado pelo usuario.
     * @param y O valor desejado pelo usuário na requisição.
     * @return "true" caso atenda; "false", caso contrario.
     */
    public boolean atende(String y)
    {
        if(!y.isEmpty())
        {
            switch(TYPE)
            {
                case NUMERICO:
                    PI_Num pi_num = (PI_Num) this;
                    return pi_num.atende(Double.parseDouble(y));
                case CAT_SIMP:
                    PI_Cat pi_cat = (PI_Cat) this;
                    return pi_cat.atende(y);
                case CAT_COMP:
                    PI_Comp pi_comp = (PI_Comp) this;
                    return pi_comp.atende(y.split(","));
            }
        }
        return false;
    }
    /**
     * Verifica se esse PI atende um valor "y", desejado pelo usuario.
     * @param y O valor desejado pelo usuário na requisição.
     * @param tol O valor da tolerancia maxima para com o valor desejado y. So utilizado para PIs numericos.
     * @return "true" caso atenda; "false", caso contrario.
     */
    public boolean atende(String y, double tol)
    {
        if(!y.isEmpty())
        {
            switch(TYPE)
            {
                case NUMERICO:
                    PI_Num pi_num = (PI_Num) this;
                    return pi_num.atende(Double.parseDouble(y), tol);
                case CAT_SIMP:
                    PI_Cat pi_cat = (PI_Cat) this;
                    return pi_cat.atende(y);
                case CAT_COMP:
                    PI_Comp pi_comp = (PI_Comp) this;
                    return pi_comp.atende(y.split(","));
            }
        }
        return false;
    }
    
    @Override
    public String toString()
    {
        switch(TYPE)
        {
            case NUMERICO:
                PI_Num pi_num = (PI_Num) this;
                return pi_num.toString();
            case CAT_SIMP:
                PI_Cat pi_cat = (PI_Cat) this;
                    return pi_cat.toString();
            case CAT_COMP:
                PI_Comp pi_comp = (PI_Comp) this;
                return pi_comp.toString();
            default:
                return "PI ???";                           
        }
    }
    @Override
    public boolean equals(Object obj) 
    {
        if(obj == null || this.getClass() != obj.getClass())  return false;
        if(this == obj)  return true;
        
        final PI other = (PI) obj;
        return Objects.equals(this.nome, other.nome);
    }
    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.nome);
        return hash;
    }
    
}
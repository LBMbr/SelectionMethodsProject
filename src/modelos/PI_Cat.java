/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package modelos;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import static modelos.Constantes.*;

public class PI_Cat extends PI
{
    /** Tipo de comportamento do PI: NB, HT, LT ou HLT. Padrao: NB.  */
    private int tipo = NB;    
    /** Valor do PI categórico simples, é uma categoria (String alfanumérica). */
    private String valor = "";    
   /** Indica se esse PI eh ordenado ou nao.  */
    private boolean ordenado = false;
    /** São as categorias que esse indicador pode assumir. */
    private final Set<Categoria> categorias = new HashSet();
    
    // # # # # # # # # Construtores # # # # # # # #    
    /**
     * 
     * @param nome
     * @param valor
     * @param peso
     * @param essencial
     * @param tipo
     * @param ordenado 
     */
    public PI_Cat(String nome, String valor, double peso, boolean essencial, int tipo, boolean ordenado) 
    {
        this.TYPE = CAT_SIMP;
        this.ordenado = ordenado;
        if(nome == null || nome.isEmpty())
            throw new IllegalArgumentException("O nome do PI deve ser válido!");
        this.nome = nome.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", ""); 
        
        if(valor == null || valor.isEmpty() || !ehStringAlfaNumerica(valor))
            throw new IllegalArgumentException("O valor do PI " + nome + " é inválido!");
        this.valor = valor.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
        
        if(peso <= 0)
            throw new IllegalArgumentException("O peso do PI deve ser maior que zero!");
        this.peso = peso;
        this.essencial = essencial;
        if(tipoValido(tipo))
            this.tipo = tipo;
    }
    
    /**
     * 
     * @param nome
     * @param grupo
     * @param tipo
     * @param categorias
     * @param ordenado 
     */
    public PI_Cat(String nome, int grupo, int tipo, Categoria[] categorias, boolean ordenado) 
    {
        this.TYPE = CAT_SIMP;
        this.ordenado = ordenado;
        if(nome == null || nome.isEmpty())
            throw new IllegalArgumentException("O nome do PI deve ser válido!");
        this.nome = nome.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
        if(grupo > 0)
            this.grupo = grupo;
        if(tipoValido(tipo))
            this.tipo = tipo;
        if(categorias == null || categorias.length == 0)
            throw new IllegalArgumentException("Quantidade de categorias insuficientes para o PI " + nome + "!");
        for(int i = 0; i < categorias.length; i++)
        {
            if(categorias[i] == null)
                throw new IllegalArgumentException("A categoria "  + (i+1) + " do PI " + nome + " é inválida!");
            
            this.categorias.add(categorias[i]);
        }
    }
    /**
     * 
     * @param nome
     * @param grupo
     * @param categorias
     * @param ordenado 
     */
    public PI_Cat(String nome, int grupo, Categoria[] categorias, boolean ordenado) 
    {
        this.TYPE = CAT_SIMP;
        this.ordenado = ordenado;
        if(nome == null || nome.isEmpty())
            throw new IllegalArgumentException("O nome do PI deve ser válido!");
        this.nome = nome.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
        if(grupo > 0)
            this.grupo = grupo;
        if(categorias == null || categorias.length == 0)
            throw new IllegalArgumentException("Quantidade de categorias insuficientes para o PI " + nome + "!");
        for(int i = 0; i < categorias.length; i++)
        {
            if(categorias[i] == null)
                throw new IllegalArgumentException("A categoria "  + (i+1) + " do PI " + nome + " é inválida!");
            
            this.categorias.add(categorias[i]);
        }
    }
            
    /**
     * 
     * @param categorias
     * @return 
     */
    public boolean insereNovasCategorias(Categoria[] categorias)
    {
        if(categorias != null)
        {
            this.categorias.clear();
            for(int i = 0; i < categorias.length; i++)
            {
                if(categorias[i] == null)
                    throw new IllegalArgumentException("A categoria "  + (i+1) + " do PI " + nome + " é inválida!");
                
                this.categorias.add(categorias[i]);
            }
            return true;
        }
        return false;
    }
    
    // GETTERS        
    /** @return O tipo do PI. */
    @Override
    public int getTipo() 
    {
        return tipo;
    }
    /** @return O valor do PI. */
    @Override
    public String getValor()
    {
        return valor;
    }
    /** @return O numero de categorias cadastradas nesse PI categorico ordenado. */
    public int getNumCat()
    {
        return this.categorias.size();
    }
    /** @return As categorias cadastradas para esse PI categorico simples. */
    @Override
    public Categoria[] getCats()
    {
        int i = 0;
        Categoria[] cats = new Categoria[categorias.size()];
        if(!categorias.isEmpty())        
            for(Iterator<Categoria> iter = this.categorias.iterator(); iter.hasNext(); )
            {
                cats[i] = iter.next();
                i++;
            }
        
        return cats;
    }
    /** @return As categorias cadastradas para esse PI categorico em formato unico de string. */
    public final String getStringCats()
    {
        Categoria[] cats = this.getCats();
        String ret = "Categorias: ";
        for(int i = 0; i < cats.length; i++)
            ret += cats[i].toString();
        return ret;
    }
    /** @return A flag indicando se esse PI qualitativo eh ordenado ou nao. */
    @Override
    public boolean isOrdenado()
    {
        return ordenado;
    }
    
    // SETTERS   
    /**
     * Modifica o tipo do PI. Pode ser: NB, HB ou LB.
     * @param tipo Novo tipo para o PI.
     */
    @Override
    public void setTipo(int tipo) 
    {
        if(tipoValido(tipo))
            this.tipo = tipo;
    }
    /**
     * Modifica o valor do PI categorico ordenado.
     * @param valor Novo valor para o PI categorico ordenado.
     */    
    @Override
    public void setValor(String valor) 
    {
        if(valorValido(valor))
            this.valor = valor;
    }
    /**
     * Modifica o valor da flag indicando se esse esse PI eh ordenado ou nao.
     * @param ordenado Nova flag indicando se eh ordenado ou nao.
     */
    @Override
    public void setOrdenado(boolean ordenado)
    {
        this.ordenado = ordenado;
        if(!ordenado)
            this.tipo = NB;
    }
    
    /**
     * Verifica se o valor passado é valido para o PI categorico cujas categorias
     * possíveis já foram pré-estabelecidas.
     * @param valor O valor.
     * @return "true" caso seja valido; "false", caso contrario.
     */
    public final boolean valorValido(String valor)
    {
        if(valor == null || valor.isEmpty() || !ehStringAlfaNumerica(valor))
            return false;
        
        valor = valor.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
        return (this.categorias.contains(new Categoria(valor)));             
    }
    /**
     * Verifica se o tipo é valido para um PI categorico ordenado.
     * @param tipo O tipo.
     * @return "true" caso seja valido; "false", caso contrario.
     */
    public final boolean tipoValido(int tipo)
    {
        if(ordenado) return (tipo == DB || tipo == NB || tipo == HT || tipo == LT || tipo == HLT);
        else         return (tipo == DB || tipo == NB);
    }
        
    /**
     * Verifica se este PI categorico atende um valor "y", desejado pelo usuario.    
     * @param y O valor desejado pelo usuário na requisição.     
     * @return "true" caso atenda; "false", caso contrario.
     */
    @Override
    public final boolean atende(String y)
    {
        y = y.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
        if(ordenado)
        {
            if(this.valor.equals(y) || this.tipo == HLT)   return true; 
            
            int nlx = this.nivelCat();
            int nly = this.nivelCat(y);   
        
            if(!valorValido(y) || nlx == -1 || nly == -1)  return false;   
            
            
            if(this.tipo == HT && nlx >= nly)      return true;        
            else if(this.tipo == LT && nlx <= nly) return true;
        
            return false;
        }
        else // Nao ordenado
            return this.valor.equals(y);        
    }
    
    /**
     * Obtem o nivel da categoria do PI de valor passado.
     * @param nome O nome do valor/categoria do PI.
     * @return O nivel do valor, -1 caso ocorra algum erro ou ele não exista nas categorias.
     */
    public int nivelCat(String nome)
    {
        Categoria aux;
        nome = nome.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
        for(Iterator<Categoria> iter = this.categorias.iterator(); iter.hasNext();)
        {
            aux = iter.next();
            if(aux.getNome().equals(nome))
                return aux.getNivel();
        }
        
        return -1;
    }
    /**
     * Obtem o nivel da categoria desse PI.
     * @return O nivel do valor, -1 caso ocorra algum erro ou ele não exista nas categorias.
     */
    public int nivelCat()
    {
        Categoria aux;
        for(Iterator<Categoria> iter = this.categorias.iterator(); iter.hasNext();)
        {
            aux = iter.next();
            if(aux.getNome().equals(valor))
                return aux.getNivel();
        }
        
        return -1;
    }
        
    /** Imprime as categorias cadastradas para esse PI categorico. */
    @Override
    public void imprimeCats()
    {
        if(!this.categorias.isEmpty())
        {
            for(Iterator<Categoria> iter = this.categorias.iterator(); iter.hasNext();)
                System.out.print(iter.next() + " ");
            System.out.println();
        }
        else
            System.out.println("Vazio!");
    }
        
    @Override
    public String toString()
    {
        return "PI CATEGORICO " + ( grupo > 0 ? ("(Gr " + grupo + ")") : "" ) +
                ": " + nome + " (" + imprimeTipo(tipo) + ") = " + valor +
                (peso > 0 ? (". Peso = " + peso) : "") +
                (ordenado ? ". Ordenado" : "") +
                (essencial ? ". Essencial" : "") + ". " + getStringCats() + "\n";
    }
}

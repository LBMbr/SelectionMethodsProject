/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package DB;

import modelos.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import static modelos.Constantes.*;

public class Dados_Req 
{    
    /** Array com os PIs da requisição do usuario.   */
    private final ArrayList<PI> pis = new ArrayList(); 
    
    /**
     * 
     * @param pis 
     */
    public Dados_Req(ArrayList<PI> pis)
    {
        if(pis == null || pis.isEmpty())
            throw new IllegalArgumentException("Entrada vazia!");
        
        for(int i = 0; i < pis.size(); i++)        
            if(!this.inserePI(pis.get(i)))
                throw new IllegalArgumentException("O PI " + pis.get(i).getNome() + " nao pode ser inserido!");               
    }
    /**************************************************************************  
     *           Formado do arquivo de requisição:
     * [*] nome_PI1 [tipo_PI1] <TAB> v1 [tol_1] <TAB>  w1    <QUEBRA LINHA>
     * [*] nome_PI2 [tipo_PI2] <TAB> v2 [tol_2] <TAB>  w2    <QUEBRA LINHA>
     * [*] nome_PI3 [tipo_PI3] <TAB> v3 [tol_3] <TAB>  w3    <QUEBRA LINHA>
     *   (...)                 <TAB>    (...)   <TAB> (...)  <QUEBRA LINHA>
     * [*] nome_PIm [tipo_PIm] <TAB> vm [tol_m] <TAB>  wm    <QUEBRA LINHA>
     * [...] -> Elementos Opcionais
     * "*" -> Marca esse PI como essencial: Nao use colchetes aqui!!!
    **************************************************************************/    
    /**
     * Carrega na memória os dados contidos em um arquivo no formato acima e o retorna.
     * @param nomeArq Nome do arquivo com os dados.
     * @return Os dados na memória, null caso algo deu errado.
     */
    public static final Dados_Req carregaDados(String nomeArq)
    {
        if(nomeArq == null || nomeArq.isEmpty())
            return null;
        
        try {
            FileReader fr = new FileReader(nomeArq);
            BufferedReader br = new BufferedReader(fr);                    
            ArrayList<PI> pis = new ArrayList();
            
            // Um PI por linha: [*] nome [tipo] <TAB> valor [tol] <TAB> peso 
            int tipo;
            double tol, peso;
            boolean essencial;
            String linha, campo1, campo2, nome, tipoS, valor;
            String[] dados;
            while((linha = br.readLine()) != null)
            {  
                dados = linha.split("\t");
                if(dados.length != 3)
                    throw new IllegalArgumentException("Cada PI deve ter 3 campos: nome, valor e peso."
                            + " Separados por <TAB>. Foram encontrados somente " + dados.length + " campos!\n");                
                
                essencial = false;
                campo1 = dados[0].toLowerCase().replaceAll(" ", ""); // Campo 1 = [*] nome[tipo]
                campo2 = dados[1].toLowerCase().replaceAll(" ", ""); // Campo 2 = valor [tol]                
                peso = Double.parseDouble(dados[2]);                 // Campo 3 = peso
                //System.out.println(linha + "\t" + dados[0] + " " + dados[1] + " " + dados[2] + "\n");
                if(campo1.contains("[") && campo1.contains("]"))
                {
                    if(campo1.charAt(0) == '*')
                    {
                        essencial = true;
                        nome = campo1.substring(1, campo1.indexOf('['));
                    }
                    else nome = campo1.substring(0, campo1.indexOf('['));
                    
                    /*if(nome.equals("custo"))
                        throw new IllegalArgumentException("O PI custo nao deve ser informado na requisicao!\n");*/
                    
                    tipoS = campo1.substring(campo1.indexOf('[') + 1, campo1.indexOf(']'));
                    //System.out.println("Nome: " + nome + "\nTipo: " + tipoS);
                    switch(tipoS)
                    {
                        case "nb":
                            tipo = NB;
                            break;
                        case "hb":
                            tipo = HB;
                            break;
                        case "lb":
                            tipo = LB;
                            break; 
                        case "ht":
                            tipo = HT;
                            break;
                        case "lt":
                            tipo = LT;
                            break;
                        case "hlt":
                            tipo = HLT;
                            break;
                        default:
                            System.out.println("AVISO: Tipo de indicador desconhecido. O padrão será utilizado!");
                            tipo = DB;
                            break;
                    }
                }
                else // nome
                {
                    if(campo1.charAt(0) == '*')
                    {
                        essencial = true;
                        nome = campo1.substring(1);
                    }
                    else nome = campo1;
                    
                    tipo = DB; // Utiliza o padrão do banco de dados
                    //System.out.println("Nome: " + nome);
                }
                // Campo 2:  valor[tolerancia]
                if(campo2.contains("[") && campo2.contains("]"))
                {
                    valor = campo2.substring(0, campo2.indexOf('['));
                    tol = Double.parseDouble(campo2.substring(campo2.indexOf('[') + 1, campo2.indexOf(']')));
                    //System.out.println("Valor: " + valor + "\nTol: " + tol);
                }
                else // Campo 2:  valor
                {
                    valor = campo2;
                    tol = 0; // Sem tolerancia
                    //System.out.println("Valor: " + valor);
                }
                //System.out.println("Nome: " + nome + "; Tipo: " + imprimeTipo(tipo) + "; Valor: " + valor + "; Tol: " + tol + "; Peso: " + peso + "; Essencial: " + essencial);
                // Tentar supor o tipo do PI pelo seu tipo de valor:
                try {
                    // Numerico?
                    PI_Num pi_num = new PI_Num(nome, tipo, Double.parseDouble(valor), tol, peso, essencial);
                    pis.add(pi_num);
                    //pis.add(new PI(nome, tipo, valor, tol, peso, essencial));
                }
                catch(NumberFormatException ex)
                {   // Nao eh numerico, eh categorico. Categorico simples ou composto?
                    if(valor.contains(","))
                    {   // Eh composto
                        PI_Comp pi_comp = new PI_Comp(nome, valor.split(","), peso, essencial);                        
                        pis.add(pi_comp);
                    }
                    else
                    {   // Possivelmente não eh composto. Ordenado? tipo != DB?
                        PI_Cat pi_cat = new PI_Cat(nome, valor, peso, essencial, tipo, (tipo != DB));
                        pis.add(pi_cat);
                    }
                }                
            } // Fim while
            br.close();
            
            return (new Dados_Req(pis));
        }
        catch (IOException ex)
        {
            System.err.println("Erro ao abrir arquivo " + nomeArq + ": " + ex.getMessage());
            return null;
        }
    }
    // GETTERS    
    /**
     * Obtem um PI da requisição na posicao desejada.
     * @param pos A posição do PI.
     * @return O PI na posição passada.
     */
    public final PI getPiPos(int pos)
    {
        return pis.get(pos);
    }    
    /**     
     * @return O número de Pis da requisição.
     */
    public final int getNumPis()
    {
        return pis.size();
    }     
    /**
     * Obtém um PI da requisição pelo nome.
     * @param nomePI Nome do PI.
     * @return O PI de nome passado.
     */
    public PI getPi(String nomePI)
    {
        String nome = nomePI.toLowerCase().replaceAll(" ", "").replaceAll("\t", "");
        for(int i = 0; i < this.pis.size(); i++)
            if(this.pis.get(i).getNome().equals(nome))
                return this.pis.get(i);
        
        return null;
    }
    /**
     * Calcula a soma dos pesos dos PIs na requisição.
     * @return A soma dos pesos de todos os PIs da requisição.
     */
    public final double getSomaPesos() 
    {
        double soma = 0.00;   
        // Cálculo da soma dos pesos na requisição
        for(int i = 0; i < this.pis.size(); i++)
            soma += this.pis.get(i).getPeso();
                
        return soma;
    }
    /**
     * Verifica se o PI de nome passado existe na requisição.
     * @param nomePI Nome do PI.
     * @return "true" se ele existe, "false", caso contrário.
     */
    public final boolean piExiste(String nomePI)
    {
        if(nomePI == null || nomePI.isEmpty())
            throw new IllegalArgumentException("Nome de PI invalido!");
        
        String nome = nomePI.toLowerCase().replaceAll(" ", "").replaceAll("\t", "");
        for(int i = 0; i < this.pis.size(); i++)
            if(this.pis.get(i).getNome().equals(nome))
                return true;
        
        return false;
    }
    /**
     * Insere um novo PI na requisição.   
     * @param pi Pi a ser inserido.
     * @return "true" se a inserção foi bem sucedida, "false", caso contrário.
     */
    public final boolean inserePI(PI pi)
    {
        if(pi == null)
            throw new IllegalArgumentException("PI inválido!");
        // Não repete nomes
        if(piExiste(pi.getNome())) 
            return false;
        
        return this.pis.add(pi);        
    }    
    /**
     * Remove um PI da requisição.
     * @param nomePI Nome do PI.
     * @return "true" se a remoção foi bem sucedida, "false", caso contrário.
     */
    public final boolean removePI(String nomePI)
    {
        if(nomePI == null || nomePI.isEmpty() || !piExiste(nomePI))
            return false; 
        
        String nome = nomePI.toLowerCase().replaceAll(" ", "").replaceAll("\t", "");
        for(int i = 0; i < this.pis.size(); i++)
            if(this.pis.get(i).getNome().equals(nome))
            {
                this.pis.remove(i);
                return true;
            }
        
        return false;
    }
    /**
     * Remove todos os PIs da requisição.
     */
    public final void removeTudo()
    {
        this.pis.clear();       
    }    
    /**
     * Imprime todos os dados da requisição do cliente.
     */
    public final void imprimeDados()
    {   
        System.out.println("Dados da requisição:"); 
        if(!this.pis.isEmpty())           
            for(int i = 0; i < this.pis.size(); i++)
                System.out.print(this.pis.get(i)); 
        else
            System.out.println("Vazio!"); 
        
        System.out.println();
    }
}
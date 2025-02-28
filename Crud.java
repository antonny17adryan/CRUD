import java.io.*;
import java.util.Arrays;
import java.util.Scanner; // Para Arrays.copyOfRange

public class Crud {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        BancoDeDados.carregarDados();

        while (true) {
            System.out.println("\n=== MENU CADASTRO DE ALUNOS ===");
            System.out.println("1. Cadastrar novo aluno");
            System.out.println("2. Listar todos os alunos");
            System.out.println("3. Atualizar aluno");
            System.out.println("4. Excluir aluno");
            System.out.println("5. Sair");

            int opcao = sc.nextInt();
            sc.nextLine();  // Consumir a quebra de linha após o nextInt()

            switch (opcao) {
                case 1:
                    cadastrarAluno(sc);
                    break;
                case 2:
                    listarAlunos();
                    break;
                case 3:
                    atualizarAluno(sc);
                    break;
                case 4:
                    excluirAluno(sc);
                    break;
                case 5:
                    System.out.println("Saindo...");
                    BancoDeDados.salvarDados();
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private static void cadastrarAluno(Scanner sc) {
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        System.out.print("Matrícula: ");
        String matricula = sc.nextLine();
        System.out.print("Curso: ");
        String curso = sc.nextLine();
        System.out.print("Nota: ");
        double nota = sc.nextDouble();
        sc.nextLine(); // Consome a quebra de linha

        if (BancoDeDados.existeMatricula(matricula)) {
            System.out.println("Erro: Matrícula já cadastrada!");
        } else {
            BancoDeDados.criar(new Aluno(nome, matricula, curso, nota));
            System.out.println("Aluno cadastrado com sucesso!");
        }
    }

    private static void listarAlunos() {
        Aluno[] lista = BancoDeDados.lerTodosAlunos();
        System.out.println("\n--- Alunos Cadastrados ---");
        for (Aluno a : lista) {
            if (a != null) {  // Verifica se o aluno não é nulo
                System.out.println("Matrícula: " + a.getMatricula());
                System.out.println("Nome: " + a.getNome());
                System.out.println("Curso: " + a.getCurso());
                System.out.println("Nota: " + a.getNota());
                System.out.println("-----------------------------");
            }
        }
    }

    private static void atualizarAluno(Scanner sc) {
        System.out.print("Matrícula do aluno para atualizar: ");
        String matricula = sc.nextLine();
        System.out.print("Novo nome: ");
        String novoNome = sc.nextLine();
        System.out.print("Novo curso: ");
        String novoCurso = sc.nextLine();
        System.out.print("Nova nota: ");
        double novaNota = sc.nextDouble();
        sc.nextLine();

        try {
            BancoDeDados.atualizarAluno(matricula, novoNome, novoCurso, novaNota);
            System.out.println("Aluno atualizado com sucesso!");
        } catch (RuntimeException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void excluirAluno(Scanner sc) {
        System.out.print("Matrícula do aluno para excluir: ");
        String matricula = sc.nextLine();
        BancoDeDados.deletarAluno(matricula);
        System.out.println("Aluno excluído com sucesso!");
    }

    static class Aluno {
        private String nome;
        private String matricula;
        private String curso;
        private double nota;

        public Aluno(String nome, String matricula, String curso, double nota) {
            this.nome = nome;
            this.matricula = matricula;
            this.curso = curso;
            this.nota = nota;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getMatricula() {
            return matricula;
        }

        public void setMatricula(String matricula) {
            this.matricula = matricula;
        }

        public String getCurso() {
            return curso;
        }

        public void setCurso(String curso) {
            this.curso = curso;
        }

        public double getNota() {
            return nota;
        }

        public void setNota(double nota) {
            this.nota = nota;
        }
    }

    static class BancoDeDados {
        private static Aluno[] alunos = new Aluno[100]; // Definindo uma matriz de tamanho fixo
        private static int contador = 0; // Contador para controlar o número de alunos

        public static boolean existeMatricula(String matricula) {
            for (int i = 0; i < contador; i++) {
                if (alunos[i].getMatricula().equals(matricula)) {
                    return true;
                }
            }
            return false;
        }

        public static void criar(Aluno aluno) {
            if (contador < alunos.length) {
                alunos[contador++] = aluno; // Adiciona o aluno à matriz e incrementa o contador
            }
        }

        public static Aluno[] lerTodosAlunos() {
            return Arrays.copyOfRange(alunos, 0, contador); // Retorna apenas os alunos cadastrados
        }

        public static void atualizarAluno(String matricula, String novoNome, String novoCurso, double novaNota) {
            for (int i = 0; i < contador; i++) {
                if (alunos[i].getMatricula().equals(matricula)) {
                    alunos[i].setNome(novoNome);
                    alunos[i].setCurso(novoCurso);
                    alunos[i].setNota(novaNota);
                    return;
                }
            }
            throw new RuntimeException("Aluno não encontrado!");
        }

        public static void deletarAluno(String matricula) {
            for (int i = 0; i < contador; i++) {
                if (alunos[i].getMatricula().equals(matricula)) {
                    // Desloca os alunos para remover o aluno excluído
                    for (int j = i; j < contador - 1; j++) {
                        alunos[j] = alunos[j + 1];
                    }
                    alunos[--contador] = null; // Decrementa o contador e coloca null no final
                    return;
                }
            }
            throw new RuntimeException("Aluno não encontrado!");
        }

        public static void salvarDados() {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("alunos.txt"))) {
                for (int i = 0; i < contador; i++) {
                    Aluno a = alunos[i];
                    writer.write(a.getMatricula() + "," + a.getNome() + "," + a.getCurso() + "," + a.getNota());
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("Erro ao salvar dados: " + e.getMessage());
            }
        }

        public static void carregarDados() {
            try (BufferedReader reader = new BufferedReader(new FileReader("alunos.txt"))) {
                String linha;
                while ((linha = reader.readLine()) != null) {
                    String[] dados = linha.split(",");
                    String matricula = dados[0];
                    String nome = dados[1];
                    String curso = dados[2];
                    double nota = Double.parseDouble(dados[3]);
                    Aluno aluno = new Aluno(nome, matricula, curso, nota);
                    criar(aluno);
                }
            } catch (IOException e) {
                System.out.println("Erro ao carregar dados: " + e.getMessage());
            }
        }
    }
}

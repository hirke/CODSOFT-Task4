import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

class Question {
    private String questionText;
    private List<String> options;
    private String correctAnswer;

    public Question(String questionText, List<String> options, String correctAnswer) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}

class QuizTimer {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private int timeLeft;

    public QuizTimer(int seconds) {
        this.timeLeft = seconds;
    }

    public void startTimer(Runnable timeoutAction) {
        scheduler.scheduleAtFixedRate(() -> {
            if (timeLeft <= 0) {
                timeoutAction.run();
                scheduler.shutdown();
            } else {
                System.out.println("Time left: " + timeLeft + " seconds");
                timeLeft--;
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void stopTimer() {
        scheduler.shutdownNow();
    }
}

public class Quiz {
    private List<Question> questions;
    private int score;
    private int currentQuestionIndex;
    private Scanner scanner;

    public Quiz(List<Question> questions) {
        this.questions = questions;
        this.score = 0;
        this.currentQuestionIndex = 0;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (currentQuestionIndex < questions.size()) {
            displayQuestion(questions.get(currentQuestionIndex));
            startQuestionTimer();
        }
        showResults();
    }

    private void displayQuestion(Question question) {
        System.out.println("Question: " + question.getQuestionText());
        List<String> options = question.getOptions();
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ": " + options.get(i));
        }
    }

    private void startQuestionTimer() {
        QuizTimer timer = new QuizTimer(10);
        timer.startTimer(this::moveToNextQuestion);
        try {
            int answer = scanner.nextInt();
            timer.stopTimer();
            checkAnswer(answer - 1);
            moveToNextQuestion();
        } catch (Exception e) {
            System.out.println("Time's up or invalid input! Moving to the next question.");
            moveToNextQuestion();
        }
    }

    private void checkAnswer(int selectedIndex) {
        Question currentQuestion = questions.get(currentQuestionIndex);
        String selectedOption = currentQuestion.getOptions().get(selectedIndex);
        if (selectedOption.equals(currentQuestion.getCorrectAnswer())) {
            score++;
        }
    }

    private void moveToNextQuestion() {
        currentQuestionIndex++;
    }

    private void showResults() {
        System.out.println("Quiz Over!");
        System.out.println("Your Score: " + score + "/" + questions.size());
    }

    public static void main(String[] args) {
        List<Question> quizQuestions = new ArrayList<>();
        quizQuestions.add(new Question("What is the capital of France?",
                Arrays.asList("Berlin", "Paris", "Rome", "Madrid"), "Paris"));
        quizQuestions.add(new Question("Which planet is known as the Red Planet?",
                Arrays.asList("Earth", "Mars", "Jupiter", "Saturn"), "Mars"));
        quizQuestions.add(new Question("Who wrote 'Hamlet'?",
                Arrays.asList("Charles Dickens", "William Shakespeare", "Leo Tolstoy", "Mark Twain"), "William Shakespeare"));
        quizQuestions.add(new Question("What is the largest ocean on Earth?",
                Arrays.asList("Atlantic Ocean", "Indian Ocean", "Arctic Ocean", "Pacific Ocean"), "Pacific Ocean"));
        quizQuestions.add(new Question("What is the chemical symbol for water?",
                Arrays.asList("HO", "H2O", "O2H", "OH"), "H2O"));

        Quiz quiz = new Quiz(quizQuestions);
        quiz.start();
    }
}


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class CalculatorAssignment extends JFrame {
    private JTextField inputField;
    private JButton calculateButton;
    private JLabel resultLabel;
    private JPanel buttonPanel;
    private JPanel topPanel;

    public CalculatorAssignment() {
        setTitle("Normal Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400); // Adjusted frame size
        setLayout(new BorderLayout(10, 10));

        // Initialize components
        inputField = new JTextField();
        calculateButton = new JButton("Calculate");
        resultLabel = new JLabel("Result: ", SwingConstants.CENTER);

        // Set Font and Size for components
        Font customFont = new Font("Courier New", Font.BOLD, 22);
        inputField.setFont(customFont);
        calculateButton.setFont(new Font("Courier New", Font.PLAIN, 20));
        resultLabel.setFont(customFont);

        // Set Background and Foreground colors for result label
        resultLabel.setOpaque(true);
        resultLabel.setBackground(Color.BLACK);
        resultLabel.setForeground(Color.WHITE);

        inputField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        resultLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create a panel for number and operation buttons
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 4, 5, 5)); // 4x4 grid layout

        // Add number and symbol buttons to the panel
        String[] buttons = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", "(", ")", "+"
        };

        for (String text : buttons) {
            JButton button = new JButton(text);
            button.setFont(new Font("Courier New", Font.BOLD, 22));
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    inputField.setText(inputField.getText() + button.getText());
                }
            });
            buttonPanel.add(button);
        }

        // Create a top panel to hold the input field and result label side by side
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 2, 10, 10)); // Horizontal layout with 2 columns
        topPanel.add(inputField);
        topPanel.add(resultLabel);

        // Add components to the frame with layout
        add(topPanel, BorderLayout.NORTH); // Top section for input and result
        add(buttonPanel, BorderLayout.CENTER); // Center section for number buttons
        add(calculateButton, BorderLayout.SOUTH); // Bottom section for calculate button

        // Add ActionListener for the calculate button
        calculateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String expression = inputField.getText();
                try {
                    double result = evaluateExpression(expression);
                    resultLabel.setText("Result: " + result); // Display result beside the input field
                } catch (Exception ex) {
                    resultLabel.setText("Error: Invalid expression");
                }
            }
        });
    }

    private double evaluateExpression(String expression) {
        return new ExpressionEvaluator().evaluate(expression);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CalculatorAssignment().setVisible(true);
            }
        });
    }
}

class ExpressionEvaluator {
    public double evaluate(String expression) {
        return evaluateExpression(expression);
    }

    private double evaluateExpression(String expression) {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();
        Stack<Character> brackets = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (ch == ' ') continue;

            if (Character.isDigit(ch) || ch == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i++));
                }
                i--;
                numbers.push(Double.parseDouble(sb.toString()));
            } else if (ch == '(' || ch == '[' || ch == '{') {
                operators.push(ch);
                brackets.push(ch);
            } else if (ch == ')' || ch == ']' || ch == '}') {
                while (!operators.isEmpty() && !isMatchingPair(operators.peek(), ch)) {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                if (!operators.isEmpty() && isMatchingPair(operators.peek(), ch)) {
                    operators.pop();
                    brackets.pop();
                }
            } else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                while (!operators.isEmpty() && precedence(ch) <= precedence(operators.peek()) && !isBracket(operators.peek())) {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(ch);
            }
        }

        while (!operators.isEmpty()) {
            numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    private boolean isMatchingPair(char open, char close) {
        return (open == '(' && close == ')') ||
               (open == '[' && close == ']') ||
               (open == '{' && close == '}');
    }

    private boolean isBracket(char ch) {
        return ch == '(' || ch == ')' || ch == '[' || ch == ']' || ch == '{' || ch == '}';
    }

    private int precedence(char op) {
        if (op == '+' || op == '-') return 1;
        if (op == '*' || op == '/') return 2;
        return 0;
    }

    private double applyOperation(char op, double b, double a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/':
                if (b == 0) throw new ArithmeticException("Division by zero");
                return a / b;
        }
        return 0;
    }
}
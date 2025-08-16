import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Calc extends JFrame {
    private JTextField display;
    private JPanel panel;
    private Font defaultFont;
    private boolean start;
    private String lastCommand;
    private double result = 0;

    public Calc() {
        super("Калькуляторчик");
        Font defaultFont = new Font("Calibri", Font.BOLD, 20);
        start = true;
        lastCommand = "=";

        setSize(300,400);
        display = new JTextField("0");
        display.setFont(defaultFont);
        add(display, BorderLayout.NORTH);

        panel = new JPanel();
        panel.setBackground(Color.GREEN);
        panel.setLayout(new GridLayout(4, 4, 5, 5));
        add(panel, BorderLayout.CENTER);

        String[] buttonLabels = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+"
        };


        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(defaultFont);
            panel.add(button);
            if (Character.isDigit(label.charAt(0)) || label.equals(".")) {
                button.addActionListener(new NumberListener());
            } else {
                button.addActionListener(new OperatorListener());
            }
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private class NumberListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String input = event.getActionCommand();
            if (start) {
                display.setText(input);
                start = false;
            } else {
                display.setText(display.getText() + input);
            }
        }
    }

    private class OperatorListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String input = event.getActionCommand();
            if (start) {
                if (input.equals("-")) {
                    display.setText(input);
                    start = false;
                } else {
                    lastCommand = input;
                }
            } else {
                calculate(Double.parseDouble((display.getText())));
                lastCommand = input;
                start = true;
            }
        }
    }

    public void calculate(double x) {
        if (lastCommand.equals("+")) result += x;
        else if (lastCommand.equals("-")) result -= x;
        else if (lastCommand.equals("*")) result *= x;
        else if (lastCommand.equals("/")) result /= x;
        else if (lastCommand.equals("=")) result = x;
        display.setText("" + result);
    }

    public static void main(String[] args) {
        Calc calc = new Calc();
        System.out.println("Hello world!");
    }
}
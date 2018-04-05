/* class SpellCheck
 * 
 * COSC 102, Colgate University
 * 
 * Implements a java.swing program to search for words in a dictionary
 * based on words in an input text file.
 * 
 * SpellCheck.main() will start the program.
 * Takes one argument, the name of a text file to be used as a dictionary.
 * 
 *  java SpellCheck book.txt
 *  
 * will call your method to parse the text in book.txt and insert the
 * words in a Trie, assumed to be implemented by class WordDictionary.
 * 
 * DO NOT MODIFY THIS CODE.  Feel free to peruse to explore how the GUI works.
 */

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

import java.io.*;
import java.util.*;

public class SpellCheck extends JPanel implements ActionListener, CaretListener {

    // Actionable JComponents, including the WebBrowserHelper
    private JButton spell = new JButton("Spell");
    private JTextField word = new JTextField(15);
    private JTextField dist = new JTextField("2", 3);
 private JTextField num_results = new JTextField("10", 3);
    private JTextArea display = new JTextArea();
    
    // the dictionary
    private WordDictionary dict;
    
    
    // for textlistener
    private String curText = "";
    
    // Constructor;  creates window elements
    public SpellCheck(WordDictionary dict) {
        
        this.dict = dict;
        
        setLayout(new BorderLayout());

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout());

        // Word text field
        word.setActionCommand("Spell");
        word.addActionListener(this);
        word.addCaretListener(this);
        toolbar.add(word);
        
        // Spell button
        spell.setActionCommand("Spell");
        spell.addActionListener(this);
        toolbar.add(spell);
        
  // Hamming distance
        toolbar.add(new JLabel("Hamming distance:"));
        toolbar.add(dist);
  
  // Number of results
  toolbar.add(new JLabel("# results:"));
  toolbar.add(num_results);

        // add toolbar to the layout
        add(toolbar, BorderLayout.PAGE_START);
        
        // HTML pane
        display.setEditable(false);
        JScrollPane sp = new JScrollPane(display);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sp.setPreferredSize(new Dimension(250,250));
        sp.setMinimumSize(new Dimension(10, 10));
        
        add(sp, BorderLayout.CENTER);
    }
    
    // event handlers
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Spell")) {

            int levels;
            try {
                levels = Integer.parseInt(dist.getText());
                if (levels < 0) {
                    display.setText("Hamming distance must be a positive integer");
                    return;
                }
            } catch (NumberFormatException xe) {
                display.setText("Hamming distance must be a positive integer");
                return;
            }
            
   int NUM_RESULTS;
   try {
    NUM_RESULTS = Integer.parseInt(num_results.getText());
   } catch (NumberFormatException xe) {
    display.setText("Specified number of results must be a positive integer");
    return;
   }
   if (NUM_RESULTS <= 0) {
    display.setText("Specified number of results must be a positive integer");
    return;
   }

            Collection<String> arr = dict.findClosest(word.getText().trim().toLowerCase(), levels, NUM_RESULTS);
            
            if (arr == null || arr.isEmpty())
                display.setText("No suggestions found");
            else {
                StringBuilder disp = new StringBuilder();
                for (String s : arr) {
                    disp.append(s);
                    disp.append('\n');
                }
                    
                display.setText(disp.toString());
            }
        }
    }
    
    public void caretUpdate(CaretEvent e) {
        String newText = word.getText();

        if (!curText.equals(newText)) {
            curText = newText;

   int NUM_RESULTS;
   try {
    NUM_RESULTS = Integer.parseInt(num_results.getText());
   } catch (NumberFormatException xe) {
    display.setText("Specified number of results must be a positive integer");
    return;
   }
   if (NUM_RESULTS <= 0) {
    display.setText("Specified number of results must be a positive integer");
    return;
   }
   
            if (curText.matches(".*[^a-zA-Z].*"))
                display.setText("Search string must be alphabetic only.");
            else if (curText.equals(""))
                display.setText("");
            else {
                Collection<String> arr = dict.getCompletions(curText.toLowerCase(), NUM_RESULTS);
                
                if (arr == null || arr.isEmpty())
                    display.setText("No matches found");
                else {
                    StringBuilder disp = new StringBuilder();
                    for (String s : arr) {
                        disp.append(s);
                        disp.append('\n');
                    }
                        
                    display.setText(disp.toString());
                }
                    
            }
            
        }
    }
    
    /* SpellCheck.build(WordDictionary dict, Scanner in)
     * 
     * Adds words from Scanner into the dictionary
     * All non-alphabetic characters are removed
     */
    private static int[] build(WordDictionary dict, Scanner in)
    {
        int[] count = new int[2];
    
        while(in.hasNext()) {
            StringBuilder s = new StringBuilder(in.next().trim().toLowerCase());
            
            int i = 0;
            while (i < s.length()) {
                char c = s.charAt(i);
                if (c < 'a' || c > 'z')
                    s.deleteCharAt(i);
                else
                    i++;
            }
            if (s.length() > 0) {
    count[0] += 1;
                if (dict.add(s.toString()))
     count[1] += 1;
            }
        }
        
        return count;
    }
    
    /* SpellCheck.main(String[] argv)
     * 
     * Starts the spell check GUI program.
     * argv contains the filenames to open and add to the dictionary
     */
    public static void main(String[] args)
    {
        final WordDictionary dict = new WordDictionary();
        int count = 0; int added = 0;
        
        if (args.length <= 0) {
            System.err.println("SpellCheck: no input filenames provided");
            System.exit(1);
            return;
        }
        
        // read files
        for (String arg : args) {
            
            Scanner in;
            
            try {
                in = new Scanner(new File(arg));
            } catch (FileNotFoundException e) {
                System.err.println("SpellCheck: file " + arg + " could not be opened");
                continue;
            }
            
            int[] r = build(dict, in);
   count += r[0];
   added += r[1];
        }
        
  // for debugging
  System.err.print("SpellCheck: successfully parsed ");
  System.err.print(count);
  System.err.print(" word(s) and added ");
  System.err.print(added);
  System.err.println(" word(s)");
 
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                JFrame frame = new JFrame("Spell Checker");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                frame.add(new SpellCheck(dict));
                
                frame.pack();
                frame.setVisible(true);
                frame.toFront();
            }
        });
    }
}

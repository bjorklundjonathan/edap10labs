import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import client.view.ProgressItem;
import client.view.StatusWindow;
import client.view.WorklistItem;
import network.Sniffer;
import network.SnifferCallback;
import rsa.Factorizer;
import rsa.ProgressTracker;

public class CodeBreaker implements SnifferCallback {

    private final JPanel workList;
    private final JPanel progressList;
    
    private final JProgressBar mainProgressBar;

    // -----------------------------------------------------------------------
    
    private CodeBreaker() {
        StatusWindow w  = new StatusWindow();

        workList        = w.getWorkList();
        progressList    = w.getProgressList();
        mainProgressBar = w.getProgressBar();
        //w.enableErrorChecks();

    }
    
    // -----------------------------------------------------------------------
    
    public static void main(String[] args) {

        /*
         * Most Swing operations (such as creating view elements) must be performed in
         * the Swing EDT (Event Dispatch Thread).
         * 
         * That's what SwingUtilities.invokeLater is for.
         */

        SwingUtilities.invokeLater(() -> {
            CodeBreaker codeBreaker = new CodeBreaker();
            new Sniffer(codeBreaker).start();
        });
    }

    // -----------------------------------------------------------------------

    /** Called by a Sniffer thread when an encrypted message is obtained. */
    @Override
    public void onMessageIntercepted(String message, BigInteger n) {
        WorklistItem wrkItem = new WorklistItem(n, message);
        JButton breakButton = new JButton("Break");
        JButton cancelButton = new JButton("Cancel");
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        
        ProgressItem pgItem = new ProgressItem(n, message);
        Runnable task = makeTask(message, n, threadPool, pgItem, cancelButton);

        SwingUtilities.invokeLater(() -> {
            wrkItem.add(breakButton);
            workList.add(wrkItem);
            mainProgressBar.setMaximum(mainProgressBar.getMaximum() + 1000000);
            breakButton.addActionListener(e -> {

                progressList.add(pgItem);
                workList.remove(wrkItem);
                pgItem.add(cancelButton);
                Future<?> future = threadPool.submit(task);
                
                cancelButton.addActionListener(d -> {
                    System.out.println("cancelling");
                    future.cancel(true);
                    mainProgressBar.setValue(mainProgressBar.getValue() + (1000000-pgItem.getProgressBar().getValue()));
                    pgItem.getTextArea().setText("[cancelled]");
                    pgItem.getProgressBar().setValue(pgItem.getProgressBar().getMaximum());
                    pgItem.remove(cancelButton);
                });
            });
        });
        
        System.out.println("message intercepted (N=" + n + ")...");
    }

    private Runnable makeTask(String message, BigInteger n, ExecutorService threadPool, ProgressItem pgItem, JButton cancelButton) {
        JProgressBar bar = pgItem.getProgressBar();
        JTextArea text = pgItem.getTextArea();
        Tracker tracker = new Tracker(bar, mainProgressBar);
        Runnable task = () -> {
            try {
                String cracked = Factorizer.crack(message, n, tracker);
                text.setText(cracked);
                JButton remove = new JButton("Remove");
                SwingUtilities.invokeLater(() -> {
                    remove.addActionListener(e -> {
                        progressList.remove(pgItem);
                    });
                    pgItem.add(remove);
                    pgItem.remove(cancelButton);
                    mainProgressBar.setValue(mainProgressBar.getValue() - 1000000);
                    mainProgressBar.setMaximum(mainProgressBar.getMaximum() - 1000000);
                });
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        };
        return task;
    }

    private static class Tracker implements ProgressTracker {
        int totalProgress = 0;
        JProgressBar bar;
        private JProgressBar mainProgressBar;


        public Tracker(JProgressBar bar, JProgressBar mainprProgressBar) {
            this.bar = bar;
            this.mainProgressBar = mainprProgressBar;
        }
        
        public void onProgress(int ppmDelta) {
            totalProgress += ppmDelta;
            bar.setValue(totalProgress);
            mainProgressBar.setValue(mainProgressBar.getValue() + ppmDelta);
            //System.out.println("progress = " + totalProgress + "/1000000");
        }
    }
}

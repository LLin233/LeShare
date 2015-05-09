package androidpath.ll.leshare.Helper;

import android.widget.ProgressBar;

/**
 * Created by Le on 2015/5/8.
 */
public class ProcessBarHelper {

    /**
     * @param progressBar
     */
    public static void startProcess(ProgressBar progressBar) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        progressBar.setIndeterminate(true);
    }

    /**
     * @param progressBar
     */
    public static void completeProcess(ProgressBar progressBar) {
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(ProgressBar.GONE);
    }
}

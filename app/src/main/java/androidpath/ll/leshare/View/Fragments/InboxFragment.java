package androidpath.ll.leshare.View.Fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidpath.ll.leshare.R;

/**
 * Created by Le on 2015/5/8.
 */
public class InboxFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        return rootView;
    }
}

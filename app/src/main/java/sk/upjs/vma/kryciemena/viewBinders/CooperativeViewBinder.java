package sk.upjs.vma.kryciemena.viewBinders;

import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import sk.upjs.vma.kryciemena.R;
import sk.upjs.vma.kryciemena.gameLogic.Card;
import sk.upjs.vma.kryciemena.gameLogic.Game;

public class CooperativeViewBinder implements SimpleAdapter.ViewBinder {
    @Override
    public boolean setViewValue(View view, Object o, String s) {
        if (view.getId() != R.id.cardTextView) {
            return false;
        }

        WindowManager wm = (WindowManager) view.getContext().getSystemService(view.getContext().WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int height = metrics.heightPixels;
        height = (int)(0.9 * ((height * 5) / (7 * 5.0)));

        TextView textView = (TextView) view;
        textView.setMinHeight(height);
        Card card = (Card) o;

        if (!card.isGuessed()) {
            return false;
        }

        if (card.getType() == Game.KILLER) {
            textView.setBackgroundColor(textView.getResources().getColor(R.color.Killer));
        }
        if (card.getType() == Game.PASSERBY) {
            textView.setBackgroundColor(textView.getResources().getColor(R.color.Passerby));
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        if (card.getType() == Game.TEAM_0) {
            textView.setBackgroundColor(textView.getResources().getColor(R.color.Team0Guessed));
            textView.setTextColor(textView.getResources().getColor(R.color.White));
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        if (card.getType() == Game.TEAM_1) {
            textView.setBackgroundColor(textView.getResources().getColor(R.color.Team1Guessed));
            textView.setTextColor(textView.getResources().getColor(R.color.White));
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        return false;
    }
}

package au.com.tyo.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import au.com.tyo.android.R;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 3/8/17.
 */

public class ListItemFactory extends InflaterFactory {

    private int resId;

    private View.OnClickListener listener;

    public ListItemFactory(Context context) {
        this(context, R.layout.image_text_list_cell);
    }

    public ListItemFactory(Context context, int resId) {
        super(context);
        this.resId = resId;
    }

    public View.OnClickListener getListener() {
        return listener;
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    /**

     define this in the listitem implementation

        @Override
        public int getViewType() {
            return ListWithHeadersAdatper.ItemType.ITEM.ordinal();
        }

     */

    @Override
    public View getView(View convertView, ViewGroup parent) {

        View view;
        if (null == convertView)
            view = inflater.inflate(resId, parent, false);
        else
            view = convertView;

        return view;
    }

    @Override
    public void bindData(View view, Object obj) {
        TextView tvTitle = (TextView) view.findViewById(android.R.id.text1);

        if (obj instanceof ListItem){
            ListItem item = (ListItem) obj;

            if (null != tvTitle)
                tvTitle.setText(item.getText1());

            ImageView imgView = (ImageView) view.findViewById(R.id.itl_image_view);
            if (null != imgView && null != item.getImageViewDrawable())
                imgView.setImageDrawable(item.getImageViewDrawable());

            ImageButton imgButton = (ImageButton) view.findViewById(R.id.itl_image_button);
            if (imgButton != null) {
                //
                imgButton.setOnClickListener(listener);
            }

            CharSequence text2 = item.getText2();

            if (text2 != null) {
                TextView tv2 = (TextView) view.findViewById(android.R.id.text2);
                tv2.setText(text2);
            }
        }
        else {
            tvTitle.setText(obj.toString());
        }
    }
}

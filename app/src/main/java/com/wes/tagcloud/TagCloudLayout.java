package com.wes.tagcloud;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by wangyong on 15/11/19.
 */
public class TagCloudLayout extends FrameLayout implements ViewTreeObserver.OnGlobalLayoutListener ,Comparator<TagCloudLayout.Holder>{

    /**
     * tags to show
     */
    private ArrayList<TagModel> tags;
    /**
     * all views
     */
    private ArrayList<Holder> views = new ArrayList<Holder>();
    private int lineNum,lineHeight;
    /**
     * view width & height
     */
    private int width ,height;
    private Random random;
    private int[] textSize = { 26, 24, 22, 18 };

    public TagCloudLayout(Context context) {
        super(context);
        init();
    }

    public TagCloudLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TagCloudLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * @param tags tags to show ,the first three's text size are largers,max size is 12
     */
    public void setKeyWords(ArrayList<TagModel> tags) {
        this.tags = tags;
    }

    private void init() {
        random = new Random();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    public void clear()
    {
        views.clear();
        removeAllViews();
    }

    public void prepare2show()
    {
        lineHeight = getTexts();
        lineNum = Math.min(height / lineHeight, 7);

        List<Integer> listY = getListY();
        for (int i =0; i< tags.size();i++)
        {
            //set random lines for all views
            Point p = randomY(listY, i);
            views.get(i).point = p;
        }
    }

    public void show()
    {
        prepare2show();
        int yItem = height/lineNum;
        //add views line by line
        for (int i =0; i< lineNum;i++)
        {
            int y = i* yItem;
            List<Holder> holders = getHoldersByY(y);
            //if there is only one ,set a random x
            if (holders.size() == 1){
                Holder holder = holders.get(0);
                //if is the first one ,put it close to quadrant 2,so the ui looks more beautiful
                if (i == 0)
                    holder.point.x = (width - holder.length)/4 + random.nextInt((width - holder.length)/4);
                else
                    holder.point.x = (width - holder.length)/3 + random.nextInt((width - holder.length)/3);
                //random a little y in this line ,so it looks more random
                if (holder.height <yItem && i != lineNum -1){
                    holder.point.y += random.nextInt(yItem - holder.height);
                }
            }else if (holders.size() == 2){
                Holder holder0 = holders.get(0);
                Holder holder1 = holders.get(1);
                int totalLen = holder0.length + holder1.length;

                int offX = (width - totalLen)/4 + random.nextInt((width - totalLen)/4);
                holder0.point.x += offX;
                //the second one need to add offX,or they will mix
                holder1.point.x += offX + random.nextInt((width - totalLen)/4);
            }

            for (Holder holder: holders){
                View v = holder.layout;
                FrameLayout.LayoutParams layParams = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
                layParams.gravity = Gravity.LEFT | Gravity.TOP;
                layParams.leftMargin = holder.point.x;
                layParams.topMargin = holder.point.y;
                addView(v, layParams);
            }
        }
    }

    /**
     * @param y
     * @return get holders sort with x
     */
    private List<Holder> getHoldersByY(int y)
    {
        List<Holder> list = new ArrayList<Holder>();
        for (Holder holder : views){
            if (holder.point.y == y){
                list.add(holder);
            }
        }
        Collections.sort(list,this);
        return list;
    }

    private Point randomY(List<Integer> listY,int i ) {
        Point p = new Point();
        if (i<3)
        {
            //first three we set them in center vertical
            p.y = listY.remove(2 + random.nextInt(2));
        }
        else{
            int tempIndex= -1;
            int index = -1;
            for (int ii = 0;ii<lineNum -1 ;ii++ )
            {
                tempIndex= random.nextInt(listY.size());
                int tempY = listY.get(tempIndex);
                index = containsInLine(tempY);
                if (index> -1)
                {
                    int totalLen = views.get(index).length + views.get(tempIndex).length;
                    //if totalLen to long ,we find another line to put
                    if (width - totalLen > width/8 )
                    {
                        break;
                    }
                }
            }
            //if we got a good position ,then we set x,else do nothing
            if (index >= 0)
            {
                Holder holder = views.get(index);
                //random order with 2 items
                int ranPositive = random.nextInt(2);
                if (ranPositive > 0)
                {
                    p.x = holder.length +random.nextInt(50);
                }
                else{
                    holder.point.x = views.get(i).length +random.nextInt(50);
                    p.x =0;
                }
            }
            p.y = listY.remove(tempIndex);

        }
        return p;
    }

    /**
     * @param y coordinate y to represents a line
     * @return wether there is a view in this line,if true return the index
     */
    private int containsInLine(int y){
    int index = -1;
    for (int i =0; i< views.size();i++)
    {
        Holder holder = views.get(i);
        if (holder.point.y != 0 &&  holder.point.y == y){
            index = i;
            break;
        }
    }
    return index;
}

    /**
     * @return get coordinates in Y
     */
    private List<Integer> getListY()
    {
        int yItem = height/lineNum;
        int size = tags.size();
        LinkedList<Integer> listY = new LinkedList<Integer>();
        for (int i = 0; i < size; i++) {
            if (i< lineNum)
            {
                listY.add(i * yItem);
            }
            else {
                //the second time  we start from 2,so we got more in vertical center
                listY.add((i-lineNum + 2) * yItem);
            }
        }
        return listY;
    }


    @Override
    public void onGlobalLayout() {
        int width = getWidth();
        int height = getHeight();
        if (width != this.width || height != this.height)
        {
            this.width = getWidth();
            this.height = getHeight();
            show();
        }

    }

    /**
     * @return new TextView instance, get textHeight and textWidth
     * return max Height
     */
    private int getTexts()
    {
        int maxHeight = 0 ;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (int i =0; i< tags.size();i++)
        {
            TagModel tag = tags.get(i);
            String keyword = tag.getKeywords();
            Holder holder = new Holder();

            //instance views
            View v = inflater.inflate(R.layout.tag_item,null);
            TextView txt = (TextView) v.findViewById(R.id.word_tv);
            ImageView heatImg = (ImageView) v.findViewById(R.id.img);
            ImageView bubbleImg = (ImageView) v.findViewById(R.id.img_bubble);

            RelativeLayout heatLayout = (RelativeLayout) v.findViewById(R.id.heat_layout);

            //set for wordtv
            txt.setTextColor(tag.getColor());
            txt.setText(keyword);
            txt.setTextSize(i > 2 ? textSize[3] : textSize[i]);

            // get the height& len of wordTv
            Paint paint = txt.getPaint();
            Paint.FontMetrics fm = paint.getFontMetrics();
            int strHeight = (int)Math.ceil(fm.descent - fm.ascent);
            int strWidth = (int) Math.ceil(paint.measureText(keyword));

            //get the max textsize ,so we got the max textheight
            if (i ==0)
            {
                maxHeight = strHeight;
            }
            //ref all views we need to holder
            holder.wordTv = txt;
            holder.layout = v;

            //if there is no heat to set we hide heatLayout else set the layoutparams
            if (tag.getHeat() >= 0) {
                //get the height for heatTv
                TextView heatTv = (TextView) v.findViewById(R.id.heat_tv);
                heatTv.setText(tag.getHeat()+"");
                paint = heatTv.getPaint();
                fm = paint.getFontMetrics();
                int heatHeight = (int)Math.ceil(fm.descent - fm.ascent);

                heatLayout.setLayoutParams(new RelativeLayout.LayoutParams(strHeight + strHeight / 4, strHeight + heatHeight));
                holder.length = strWidth + strHeight;
                holder.height = strHeight+heatHeight;
            }
            else
            {
                heatLayout.setVisibility(View.GONE);
                holder.length = strWidth;
                holder.height = strHeight;
            }

            views.add(holder);
        }
        return maxHeight;
    }

    @Override
    public int compare(Holder holder, Holder t1) {
        return holder.point.x - t1.point.x;
    }

    class Holder{
        public View layout;
        public TextView wordTv;
        public int length;
        public int height;
        public Point point = new Point();
    }
}

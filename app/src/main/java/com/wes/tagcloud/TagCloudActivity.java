package com.wes.tagcloud;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.util.ArrayList;


public class TagCloudActivity extends Activity implements OnClickListener {
		public static final String[] keywords = { "那英来了1", "逗比出现2", "前方高能3", "Lady gaga 4",
				"下雨了5", "蒙面歌王6", "音乐盛会7", "崔健8", "三天三夜9", "好声音学员大合唱10"};
		private ArrayList<TagModel> wordArray =  new ArrayList<TagModel>();
		private TagCloudLayout tagCloudLayout;
		private Button btn;



	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		btn = (Button) findViewById(R.id.btn);
		btn.setOnClickListener(this);
		tagCloudLayout = (TagCloudLayout) findViewById(R.id.cloudTag);
		for (int i =0; i< keywords.length;i++)
		{
			String str = keywords[i];
			TagModel tag = new TagModel();
			if (i < 3){
				tag.setColor(Color.parseColor("#e06e45"));
				tag.setHeat(95 - i * 2);
			}

			else {
				tag.setColor(Color.BLACK);
			}

			tag.setKeywords(str);
			wordArray.add(tag);
		}
		tagCloudLayout.setKeyWords(wordArray);

	}


	@Override
	public void onClick(View v) {
		if (v == btn) {
			generateAnother();
		}
	}


	private void generateAnother() {
		tagCloudLayout.clear();
		tagCloudLayout.show();
	}

}

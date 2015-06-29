package com.nct.mv;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.TextHttpResponseHandler;
import com.nct.customview.TfTextView;
import com.nct.dataloader.DataLoader;
import com.nct.dataloader.URLProvider;
import com.nct.utils.Debug;
import com.nct.utils.Utils;

import org.apache.http.Header;

import thh.com.mycouper.R;

public class AtStore extends AtBase {
	private static final String tag = "AtIntroduce";

	private Button bntLogin;
	private TfTextView txtRegister;

	private EditText edtAccount,edtPassword;

	private String userName,pass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_store);

		setLanguge();
		initTopbar(getString(R.string.stores));
		setTopbarBtLeftImage(R.drawable.icon_back);
		setTopbarBtRightVisible(View.INVISIBLE);
		setTopbarLeftBtListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		bntLogin = (Button) findViewById(R.id.at_store_bnt_login);
		bntLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loginCompany();
			}
		});

		txtRegister = (TfTextView) findViewById(R.id.at_register_tv);

		String strHtml = "No account? Please visit "
				+ "<font color='blue'><a href='GoRegister'><i>www.mycouper.com</i></a></font>"
				+ " to register. ";

		CharSequence sequence = Html.fromHtml(strHtml);
		SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
		URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
		for(URLSpan span : urls) {
			makeLinkClickable(strBuilder, span);
		}
		txtRegister.setLinksClickable(true);
		txtRegister.setLinkTextColor(Color.BLUE);
		txtRegister.setMovementMethod(LinkMovementMethod.getInstance());
		txtRegister.setText(strBuilder);
		txtRegister.setTextColor(Color.BLACK);

		edtAccount = (EditText) findViewById(R.id.store_account);
		edtAccount.setText("company7@gmail.com");
		edtPassword = (EditText) findViewById(R.id.store_password);
		edtPassword.setText("123");

	}

	protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span)
	{
		int start = strBuilder.getSpanStart(span);
		int end = strBuilder.getSpanEnd(span);
		int flags = strBuilder.getSpanFlags(span);
		ClickableSpan clickable = new ClickableSpan() {
			public void onClick(View view) {
				// Do something with span.getURL() to handle the link click...
				Intent intent = null;
				String mKeyValue = span.getURL().toString();
				if(mKeyValue.equals("GoRegister")){
					String url = "http://mycouper.com/";
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					startActivity(i);
				}
			}
		};
		strBuilder.setSpan(clickable, start, end, flags);
		strBuilder.removeSpan(span);
	}

	private void loginCompany()
	{
		userName = edtAccount.getText().toString();
		pass = edtPassword.getText().toString();
		if(!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(pass))
		{
			showDialogLoading();
			DataLoader.postParam(URLProvider.getParamsCompanyLogin(userName, pass), new TextHttpResponseHandler() {
				@Override
				public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
					hideDialogLoading();
				}
				@Override
				public void onSuccess(int i, Header[] headers, String s) {
					hideDialogLoading();
					Debug.logData(tag, s);
					//Utils.gotoScreenStoreDetail(AtStore.this);
				}
			});
		}
	}

}

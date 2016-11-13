package cz.iocb.chemweb.client.ui.main;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;



public class AboutPart extends Composite
{
    private static AboutPartUiBinder uiBinder = GWT.create(AboutPartUiBinder.class);


    interface AboutPartUiBinder extends UiBinder<Widget, AboutPart>
    {
    }


    public AboutPart()
    {
        initWidget(uiBinder.createAndBindUi(this));
    }
}

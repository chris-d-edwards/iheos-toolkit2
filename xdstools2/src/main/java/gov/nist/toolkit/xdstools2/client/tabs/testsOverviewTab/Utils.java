package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;

/**
 * Created by Diane Azais local on 10/15/2015.
 */
public class Utils {

    /**
     * Make icons available as SafeHtml
     * @param resource the image resource to transform
     * @return SafeHtml code for the image
     */
    public static SafeHtml makeImage(ImageResource resource) {
        AbstractImagePrototype proto = AbstractImagePrototype.create(resource);
        return proto.getSafeHtml();
    }

    /**
     * Make buttons available as SafeHtml
     * @param b the button
     * @return SafeHtml code for the button
     */
    public static SafeHtml makeButton(Button b){
        return SafeHtmlUtils.fromTrustedString(b.getElement().toString());
    }

}
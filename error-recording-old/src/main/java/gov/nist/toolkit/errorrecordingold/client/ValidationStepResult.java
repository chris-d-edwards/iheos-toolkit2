package gov.nist.toolkit.errorrecordingold.client;

import java.util.List;
import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Collects together a step name and a collection of ValidatorErrorItems so that
 * a single object represents the error/status output of a validation step.
 * @author bill
 *
 */
public class ValidationStepResult implements IsSerializable {
	public String stepName;
	public List<GwtValidatorErrorItem> er;

	public ValidationStepResult() {}

	public String toString() {
		StringBuffer buf = new StringBuffer();

		for (GwtValidatorErrorItem item : er)
			buf.append(item).append("\n");
		return buf.toString();
	}

}

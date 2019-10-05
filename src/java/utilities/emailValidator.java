/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author Eicke Herbertz
 */
@FacesValidator(value = "emailValidator")
public class emailValidator implements Validator{
    @Override
    public void validate(FacesContext context, UIComponent component, Object value)
                            throws ValidatorException{
        String emailString = (String)value;
        String msg, UIID = component.getClientId();
        String pattern;
        FacesMessage fm;
        
        // RFC 5322
        pattern = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^"
                + "_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21"
                + "\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e"
                + "-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+"
                + "[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4]"
                + "[0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]"
                + "?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c"
                + "\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b"
                + "\\x0c\\x0e-\\x7f])+)\\])";
        
        if (!Pattern.matches(pattern, emailString)){
            msg = "E-Mail Adresse ist ungültig!";
            fm = new FacesMessage(UIID, msg);
            throw new ValidatorException(fm);
        }
    }
}

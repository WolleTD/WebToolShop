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
@FacesValidator(value = "pwdValidator")
public class pwdValidator implements Validator{
    
    @Override
    public void validate(FacesContext context, UIComponent component, Object value)
                            throws ValidatorException{
        String passwordString = (String)value;
        String msg, UIID = component.getClientId();
        String pattern;
        FacesMessage fm;
        
        
        pattern =   "^" +
                    "(?=.*[A-Z])"+          //
                    "(?=.*[a-z])"+          //
                    "(?=.*[0-9])"+          //
                    "(?=\\S+)"+             // Keine Withespaces erlaubt
                    "(?=.*[@#%$^&§+=])"+    // Diese Sonderzeichen sind erlaubt
                    ".{8,}"+                // mindestens 8 Zeichen
                    "$";
        
        if (!Pattern.matches(pattern, passwordString)){
            msg = "Passwort nicht sicher genug!";
            fm = new FacesMessage(msg);
            throw new ValidatorException(fm);
        }
                
    }
}

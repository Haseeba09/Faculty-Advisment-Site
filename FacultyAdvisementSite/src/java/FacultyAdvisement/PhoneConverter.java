package FacultyAdvisement;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author violentsushi
 */
@FacesConverter("phoneConverter")
public class PhoneConverter implements Converter {

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
        String phoneNumber = (String) modelValue;

        return "(" + phoneNumber.substring(0, 3) + ") " + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6, 10);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
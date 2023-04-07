package BGU.Group13B.backend.storePackage.permissions;

//I would like this class to be used as mark for every function in store that Manager can use
//Mark methods with this annotation to mark them as manager functionality

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

public @interface DefaultManagerFunctionality {

}

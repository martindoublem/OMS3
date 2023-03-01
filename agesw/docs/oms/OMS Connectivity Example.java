
/**
 * This Java class is meant to demonstrate how the Object Modeling System (OMS) 
 * passes data around its components.
 *
 * OMS components are very similar to functions, i.e., they take in input and 
 * perform some operation on the input which produces output. This class  
 * demonstrates various functions to describe what OMS is doing "under the 
 * hood" so that each connection can be understood.
 *
 * The seven connection types are: (1) field2in ("field to in"), (2) field2inout 
 * ("field to in out"), (3) in2in ("in to in"), (4) out2field ("out to field"), 
 * (5) out2in ("out to in"), (6) out2out ("out to out"), and (7) val2in 
 * ("val to in")
 *
 * Note here that "=" means assignment and not a statement of equality, i.e., 
 * x = y means that x gets the value of y. x = f(y) means that x gets the 
 * value of the function evaluated with the input of y.
 *
 * @author Nathan Lighthart
 */
public class OMSConnectivityExample {
	/**
	 * Arbitrary function that takes an input parameter.
	 *
	 * @param in the input
	 */
	public static void in(long in) {
		long val = 1;
		for(int i = 0; i < in; ++i) {
			val = 2 * val;
		}
		System.out.println(val);
	}

	/**
	 * Arbitrary function that returns output.
	 *
	 * @return the output
	 */
	public static long out() {
		return 16L;
	}

	/**
	 * Arbitrary function that takes an input parameter and returns output.
	 *
	 * @param in the input
	 * @return the output
	 */
	public static long inout(long in) {
		return 2 * in;
	}

	/**
	 * Function that is required to achieve a in2in relationship.
	 *
	 * @param in the input
	 */
	public static void in2in(long in) {
		// Note that I can read or modify "in"
		// which is not possible with field2in
		for(int i = 0; i < in; ++i) {
			in(in); // Me passing it to someone else
		}
	}

	/**
	 * Function that is required to achieve an out2out relationship.
	 *
	 * @return the output
	 */
	public static long out2out() {
		// Me saying that my output is the output of some other function
		return out();
	}

	public static void main(String[] args) {
		long field = 4; // represents a field or in more common terms a variable

		// field2in
		System.out.println("field2in");
		in(field); // prints 2^4

		// field2inout
		System.out.println("field2inout");
		// Note that field is used as input and is set to the output
		// This is equivalent to doing a field2in and an out2field
		field = inout(field);
		System.out.println(field); // prints 2*4

		// in2in
		System.out.println("in2in");
		in2in(8); // prints 2^8 eight times

		// out2field
		System.out.println("out2field");
		field = out();
		System.out.println(field); // prints 16

		// out2in
		System.out.println("out2in");
		in(out()); // prints 2^16

		// out2out
		System.out.println("out2out");
		System.out.println(out2out()); // prints 16

		// val2in
		System.out.println("val2in");
		in(32); // prints 2^32
	}
}

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;

public class ShamirSecretSharing {

    public static void main(String[] args) {
        try {
            // Read and parse the JSON file
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader("test_case.json"));

            // Extract the array of test cases
            JSONArray testCasesArray = (JSONArray) jsonObject.get("test_cases");

            // Loop through each test case
            for (int t = 0; t < testCasesArray.size(); t++) {
                JSONObject testCase = (JSONObject) testCasesArray.get(t);

                // Extract n and k from the "keys" object
                JSONObject keysObject = (JSONObject) testCase.get("keys");
                int n = Integer.parseInt(keysObject.get("n").toString());
                int k = Integer.parseInt(keysObject.get("k").toString());

                // Prepare lists for storing the x and y coordinates
                ArrayList<BigInteger> xList = new ArrayList<>();
                ArrayList<BigInteger> yList = new ArrayList<>();

                // Read each root (x, y pair) and decode the y value
                for (int i = 1; i <= n; i++) {
                    String baseKey = String.valueOf(i);
                    if (testCase.containsKey(baseKey)) {
                        JSONObject rootObject = (JSONObject) testCase.get(baseKey);

                        // Extract base and value
                        int base = Integer.parseInt(rootObject.get("base").toString());
                        String valueStr = rootObject.get("value").toString();

                        // Decode the y value based on the provided base
                        BigInteger yDecoded = new BigInteger(valueStr, base);

                        // x value is the index (i), y value is the decoded value
                        xList.add(BigInteger.valueOf(i));
                        yList.add(yDecoded);
                    }
                }

                // Ensure we have at least k points
                if (xList.size() < k || yList.size() < k) {
                    System.out.println("Test case " + (t + 1) + ": Not enough points to solve for the polynomial.");
                    continue;
                }

                // Use Lagrange interpolation to find the constant term (c)
                BigInteger constantTerm = lagrangeInterpolation(xList, yList, k);
                System.out.println("Test case " + (t + 1) + ": The constant term (c) is: " + constantTerm);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Function for Lagrange Interpolation to find the constant term (c)
    public static BigInteger lagrangeInterpolation(ArrayList<BigInteger> xList, ArrayList<BigInteger> yList, int k) {
        BigInteger result = BigInteger.ZERO;

        // Loop through each point to compute the Lagrange basis polynomials
        for (int i = 0; i < k; i++) {
            BigInteger xi = xList.get(i);
            BigInteger yi = yList.get(i);

            // Compute the Lagrange basis polynomial L_i(x)
            BigInteger li = BigInteger.ONE;
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    BigInteger xj = xList.get(j);
                    li = li.multiply(xj).divide(xj.subtract(xi));
                }
            }

            // Add the term to the result
            result = result.add(yi.multiply(li));
        }

        // The result is the constant term (c)
        return result;
    }
}

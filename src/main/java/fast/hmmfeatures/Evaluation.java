package fast.hmmfeatures;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Evaluation {

	public static Opts opts;
	public static boolean verbose = false;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public Evaluation(Opts opts) {
		this.opts = opts;
	}

	// one fold all skills
	public static int doEvaluationAndWritePred(ArrayList<Double> probs,
			ArrayList<Integer> labels, ArrayList<Integer> actualLabels,
			ArrayList<Integer> trainTestIndicator) throws IOException {
		// evaluate and print out
		if (probs.size() != labels.size() || labels.size() != actualLabels.size()
				|| actualLabels.size() != trainTestIndicator.size()) {
			System.out.println("Error: doEvluation size mismatch!");
			System.exit(1);
		}

		double rmse = 0;
		double accuracy = 0;
		double sumSquareError = 0;
		double nbMisclassification = 0;
		double correctRatio = 0.0;
		BufferedWriter predWriter = new BufferedWriter(new FileWriter(
				opts.predictionFile));
		predWriter.write("actualLabel,predLabel, predProb\n");

		// System.out.println("number of intances: " + probs.size());
		int realSize = 0;
		for (int i = 0; i < probs.size(); i++) {
			if (verbose) {
				System.out.println(i + "th:\tpredict:" + probs.get(i) + ","
						+ labels.get(i) + "\tactual:" + actualLabels.get(i));
			}
			if (trainTestIndicator.get(i) != -1) {
				realSize++;
				predWriter.write(actualLabels.get(i) + "," + labels.get(i) + ","
						+ probs.get(i) + "\n");
				sumSquareError += Math.pow((actualLabels.get(i) - probs.get(i)), 2);
				nbMisclassification += Math.abs(actualLabels.get(i) - labels.get(i));
				if (actualLabels.get(i) == 1)
					correctRatio++;
			}
		}
		predWriter.flush();
		predWriter.close();
		rmse = Math.pow(sumSquareError / realSize, 0.5);
		accuracy = (realSize - nbMisclassification) / realSize;
		correctRatio = correctRatio / realSize;
		String str = "\nTest Results:\n\trmse=\t" + rmse + "\taccuracy=\t"
				+ accuracy + "\tcorrectClassRatio=\t" + correctRatio
				+ "\t#testObservations=\t" + realSize;
		// String str = "\nTest Results:\n\tcorrectClassRatio=" + correctRatio;
		System.out.println(str);
		if (opts.writeMainLog) {
			opts.mainLogWriter.write(str + "\n");
			opts.mainLogWriter.flush();
		}
		return realSize;
	}

}

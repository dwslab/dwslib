package de.dwslab.dwslib.aws;

import java.net.URL;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import de.dwslab.dwslib.aws.S3Permission.PermissionType;
import de.dwslab.dwslib.aws.S3Permission.UserGroup;

public class ParallelS3 {

	public static void main(String[] args) {
		// TODO load configuration
		S3Credentials cred = new S3Credentials("", "");

		/* set up command line parameters */
		Options options = new Options();

		// Credentials
		Option accessKey = OptionBuilder.withArgName("accesskey").hasArg()
				.withDescription("Your S3 Access Key").create("accesskey");

		Option secretKey = OptionBuilder.withArgName("secret").hasArg()
				.withDescription("Your S3 Secret").create("secret");

		// Command target
		Option targetFile = OptionBuilder
				.withArgName("file")
				.hasArg()
				.withDescription(
						"A file containing the S3-URLs of all files to process")
				.create("targetfile");
		Option targetS3 = OptionBuilder
				.withArgName("URL")
				.hasArg()
				.withDescription(
						"A URL representing the filter to determine the S3-files to process")
				.create("targetfilter");

		// get command
		options.addOption("overwritelocal", false, "Overwrite local files");
		options.addOption("requesterpays", false,
				"Enabled download from 'requester pays' sources");

		// Option cmdTarget = OptionBuilder.withArgName(name)

		// General options
		int numThreads = Runtime.getRuntime().availableProcessors();

		options.addOption(accessKey);
		options.addOption(secretKey);
		options.addOption(targetFile);
		options.addOption(targetS3);
		// options.addOption(OptionBuilder.hasArgs(2).create("setacl"));

		/* parse command line parameters */
		CommandLineParser parser = new BasicParser();
		CommandLine cmd;
		try {
			ParallelS3Processor processor = null;

			cmd = parser.parse(options, args);

			String access = cmd.getOptionValue("accesskey");
			String secret = cmd.getOptionValue("secret");
			if (access != null) {
				cred.setAwsAccessKey(access);
			}
			if (secret != null) {
				cred.setAwsSecret(secret);
			}

			CommandTarget target = parseTarget(cmd);

			if (cmd.getArgs()[0].equals("setacl") && cmd.getArgs().length == 3) {
				String grp = cmd.getArgs()[1];
				String perm = cmd.getArgs()[2];

				S3Permission p = new S3Permission(UserGroup.valueOf(grp),
						PermissionType.valueOf(perm));

				processor = new ParallelS3SetAcl(numThreads, cred, target, p);
			}
			if (cmd.getArgs()[0].equals("ls")) {
				S3Helper s3 = new S3Helper(cred.getAwsAccessKey(),
						cred.getAwsSecret());

				List<S3File> lst = target.loadList(s3);

				for (S3File f : lst) {
					System.out.println("s3://" + f.get_bucket() + "/"
							+ f.get_key());
				}

				return;
			}
			if (cmd.getArgs()[0].equals("get") && cmd.getArgs().length == 2) {
				String localFolder = cmd.getArgs()[1];
				boolean overwriteExisting = cmd.hasOption("overwritelocal");
				boolean requesterpays = cmd.hasOption("requesterpays");

				processor = new ParallelS3Downloader(numThreads, cred, target,
						localFolder, overwriteExisting, requesterpays);
			}

			/*
			 * else if(cmd.getArgs()[0].equals("get")) {
			 * 
			 * }
			 */

			if (processor != null) {
				processor.process();
			} else {
				usage(options);
			}
		} catch (Exception e) {
			usage(options);
		}
	}

	private static void usage(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("ParallelS3 [options] <command> <arguments...>",
				options);

		System.out
				.println("\nCommands: \n\n"
						+ "setacl <group> <permission>\n"
						+ "\tChange permissions of files.\n"
						+ "\t<group>: All, Authenticated, Log\n"
						+ "\t<permission>: Read, Write, Full\n\n"

						+ "ls\n"
						+ "\tLists the files that will be processed with the given options.\n\n"

						+ "get <localfolder>\n"
						+ "\tDownload files to <localfolder>.\n"
						+ "\tuse -overwritelocal to overwrite existing files");

		System.out
				.println("\nExamples: \n\n"
						+ "ParallelS3 -targetfile filelist.txt setacl All Read\n\n"
						+ "ParallelS3 -targetfilter s3://mybucket/keyprefix setacl Authenticated Write\n\n"
						+ "ParallelS3 -targetfile filelist.txt -overwritelocal get local/folder/");
	}

	private static CommandTarget parseTarget(CommandLine cmd) throws Exception {
		CommandTarget t = null;

		if (cmd.hasOption("targetfile"))
			t = new CommandTarget(cmd.getOptionValue("targetfile"));

		if (cmd.hasOption("targetfilter")) {
			if (t != null)
				throw new Exception(
						"Only one of 'targetfile' and 'targetfilter' can be used!");

			String url_string = cmd.getOptionValue("targetfilter").replace(
					"s3://", "http://");
			URL u = new URL(url_string);
			S3FileFilter f = new S3FileFilter(u.getHost(), u.getPath()
					.replaceFirst("/", ""));
			t = new CommandTarget(f);
		}

		if (t == null)
			throw new Exception(
					"Either 'targetfile' or 'targetfilter' must be used!");

		return t;
	}
}

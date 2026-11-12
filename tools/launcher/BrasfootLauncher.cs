using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Windows.Forms;

internal static class BrasfootLauncher
{
    [STAThread]
    private static int Main()
    {
        string root = Path.GetDirectoryName(Application.ExecutablePath);
        string jar = Path.Combine(root, "brasfoot-online.jar");
        if (!File.Exists(jar)) jar = Path.Combine(root, "dist", "brasfoot-online.jar");
        if (!File.Exists(jar))
        {
            MessageBox.Show("Nao foi encontrado brasfoot-online.jar. Extraia a pasta inteira do Brasfoot Online.",
                "Brasfoot Online", MessageBoxButtons.OK, MessageBoxIcon.Error);
            return 1;
        }

        string javaw = FindJavaw();
        if (javaw == null)
        {
            MessageBox.Show("Java 8 nao encontrado. Instale o Java 8 ou configure JAVA_HOME.",
                "Brasfoot Online", MessageBoxButtons.OK, MessageBoxIcon.Warning);
            return 1;
        }

        try
        {
            Process.Start(new ProcessStartInfo
            {
                FileName = javaw,
                Arguments = "-jar " + Quote(jar) + " --root " + Quote(root),
                WorkingDirectory = root,
                UseShellExecute = false,
                CreateNoWindow = true,
                WindowStyle = ProcessWindowStyle.Hidden
            });
            return 0;
        }
        catch (Exception error)
        {
            MessageBox.Show("Nao foi possivel iniciar o Brasfoot Online:\n" + error.Message,
                "Brasfoot Online", MessageBoxButtons.OK, MessageBoxIcon.Error);
            return 1;
        }
    }

    private static string FindJavaw()
    {
        List<string> candidates = new List<string>();
        string javaHome = Environment.GetEnvironmentVariable("JAVA_HOME");
        if (!String.IsNullOrEmpty(javaHome)) candidates.Add(Path.Combine(javaHome, "bin", "javaw.exe"));
        candidates.Add(Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.CommonProgramFilesX86), "Oracle", "Java", "java8path", "javaw.exe"));

        string[] programFiles = {
            Environment.GetFolderPath(Environment.SpecialFolder.ProgramFiles),
            Environment.GetFolderPath(Environment.SpecialFolder.ProgramFilesX86)
        };
        string[] patterns = {
            Path.Combine("Java", "jre1.8*"), Path.Combine("Java", "jdk1.8*"),
            Path.Combine("Eclipse Adoptium", "jdk-8*"), Path.Combine("Eclipse Adoptium", "jre-8*"),
            Path.Combine("Microsoft", "jdk-8*"), Path.Combine("Amazon Corretto", "jdk8*")
        };
        foreach (string basePath in programFiles)
        {
            if (String.IsNullOrEmpty(basePath)) continue;
            foreach (string pattern in patterns)
            {
                string parent = Path.GetDirectoryName(Path.Combine(basePath, pattern));
                string leaf = Path.GetFileName(pattern);
                if (!Directory.Exists(parent)) continue;
                foreach (string install in Directory.GetDirectories(parent, leaf).OrderByDescending(path => path))
                    candidates.Add(Path.Combine(install, "bin", "javaw.exe"));
            }
        }

        string pathValue = Environment.GetEnvironmentVariable("PATH") ?? String.Empty;
        foreach (string entry in pathValue.Split(Path.PathSeparator))
            if (!String.IsNullOrWhiteSpace(entry)) candidates.Add(Path.Combine(entry.Trim(), "javaw.exe"));

        foreach (string candidate in candidates)
            if (File.Exists(candidate)) return candidate;
        return null;
    }

    private static string Quote(string value)
    {
        return "\"" + value.Replace("\"", "\\\"") + "\"";
    }
}

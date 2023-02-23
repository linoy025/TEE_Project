using Intel.Dal;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace TryTeeHost
{
    public partial class MainForm : Form
    {
        public string encryptedDataFilePath = "";
        public string encryptedDataFolder = "C:\\Users\\tam05\\My Documents\\Visual Studio 2015\\Projects\\Data";
        private const int MAX_SAMPLE_DATA_SIZE = 4096;
        int LOAD_DATA_CMD = 2;

     





        public List<Person> person = new List<Person>();
        public MainForm()
        {
            InitializeComponent();
          //  InitializePerson(ref person);
            label1.Text = "Enter Id";
            label2.Text = "Enter a location";
            button1.Text = "Add person to our data base";
            button2.Text = "Check if this person was in this location";

            initJHI();


        }


         Jhi jhi = Jhi.Instance;
        JhiSession session;

        public void initJHI()
        {

#if AMULET
            // When compiled for Amulet the Jhi.DisableDllValidation flag is set to true 
            // in order to load the JHI.dll without DLL verification.
            // This is done because the JHI.dll is not in the regular JHI installation folder, 
            // and therefore will not be found by the JhiSharp.dll.
            // After disabling the .dll validation, the JHI.dll will be loaded using the Windows search path
            // and not by the JhiSharp.dll (see http://msdn.microsoft.com/en-us/library/7d83bc18(v=vs.100).aspx for 
            // details on the search path that is used by Windows to locate a DLL) 
            // In this case the JHI.dll will be loaded from the $(OutDir) folder (bin\Amulet by default),
            // which is the directory where the executable module for the current process is located.
            // The JHI.dll was placed in the bin\Amulet folder during project build.
            Jhi.DisableDllValidation = true;
#endif

          

            // This is the UUID of this Trusted Application (TA).
            //The UUID is the same value as the applet.id field in the Intel(R) DAL Trusted Application manifest.
            string appletID = "24a6b77f-d7f1-4a50-9377-481c16dc3d87";
            // This is the path to the Intel Intel(R) DAL Trusted Application .dalp file that was created by the Intel(R) DAL Eclipse plug-in.
            //string appletPath = "C:/Users/tam05/workspaceTEE\\TryTee\\bin\\TryTee-debug.dalp";

            string appletPath = "C:/Users/tam05/workspaceTEE\\TryTee\\bin\\TryTee.dalp";




            // Install the Trusted Application
           // Console.WriteLine("Installing the applet.");
            jhi.Install(appletID, appletPath);



            // Start a session with the Trusted Application
            byte[] initBuffer = new byte[] { }; // Data to send to the applet onInit function
            Console.WriteLine("Opening a session.");
            jhi.CreateSession(appletID, JHI_SESSION_FLAGS.None, initBuffer, out session);
        }

        //public void InitializePerson(ref List<Person> person)
        //{
        //    person = new List<Person>()
        //    {
        //        new Person()
        //        {
        //            Id = 123456789,
                    
        //        },
        //        new Person()
        //        {
        //             Id = 123456789,
                    
        //        },
        //    };
        //}

        private void button1_Click(object sender, EventArgs e)
        {
           Person person = new Person();
            person.Id = textBox1.Text.ToString();
        person.locations = textBox2.Text.ToString();
         
                // Send and Receive data to/from the Trusted Application
                byte[] sendBuff = UTF32Encoding.UTF8.GetBytes(person.locations); // A message to send to the TA
                byte[] recvBuff = new byte[2000]; // A buffer to hold the output data from the TA
                int responseCode; // The return value that the TA provides using the IntelApplet.setResponseCode method
                int cmdId = 1; // The ID of the command to be performed by the TA
                Console.WriteLine("Performing send and receive operation.");
                jhi.SendAndRecv2(session, cmdId, sendBuff, ref recvBuff, out responseCode);


                string txt = UTF32Encoding.UTF8.GetString(recvBuff);

                //string encryptedDataFilePath = "";
                //string encryptedDataFolder = "C:\\Users\\tam05\\My Documents\\Visual Studio 2015\\Projects\\Data";
                if (!Directory.Exists(encryptedDataFolder))
                    Directory.CreateDirectory(encryptedDataFolder);
                encryptedDataFilePath = Path.Combine(encryptedDataFolder, person.Id + ".bin");

                File.WriteAllBytes(encryptedDataFilePath, recvBuff);


                MessageBox.Show("This person and location were added succesfully", "success", MessageBoxButtons.OK, MessageBoxIcon.Information);

            









            //
            //byte[] sendBuff1 = UTF32Encoding.UTF8.GetBytes(textBox2.Text); // A message to send to the TA
            //jhi.SendAndRecv2(session, cmdId, sendBuff1, ref recvBuff, out responseCode);

            //Console.Out.WriteLine("Response buffer is " + UTF32Encoding.UTF8.GetString(recvBuff));

            //// Close the session
            //Console.WriteLine("Closing the session.");
            //jhi.CloseSession(session);

            ////Uninstall the Trusted Application
            //Console.WriteLine("Uninstalling the applet.");
            //jhi.Uninstall(appletID);

            //Console.WriteLine("Press Enter to finish.");
            //Console.Read();
        }

        public bool Chack(string a, string b)
        {
            string encryptedDataFilePath = Path.Combine(encryptedDataFolder, (a + ".bin"));
            if (!File.Exists(encryptedDataFilePath))
                MessageBox.Show("This person doesn't exist in our database", "wrong id", MessageBoxButtons.OK, MessageBoxIcon.Error);
            else
            {

                // Read the encrypted data from the file
                byte[] encryptedData = File.ReadAllBytes(encryptedDataFilePath);

                // Ask the applet to decrypt the data
                int responseCode;
                byte[] decryptedData = new byte[MAX_SAMPLE_DATA_SIZE];
                jhi.SendAndRecv2(session, LOAD_DATA_CMD, encryptedData, ref decryptedData, out responseCode);
                string txt = UTF32Encoding.UTF8.GetString(decryptedData);
                if (txt == b)
                {
                    return true;
                }
               
            }
            return false;
        }


        private void button2_Click(object sender, EventArgs e)
        {


            //first find if the file exists
            //if the file does not exists show message not exist
          string  encryptedDataFilePath = Path.Combine(encryptedDataFolder, (textBox1.Text + ".bin"));
            if (!File.Exists(encryptedDataFilePath))
                MessageBox.Show("This person doesn't exist in our database", "wrong id", MessageBoxButtons.OK, MessageBoxIcon.Error);
            else
            {

                // Read the encrypted data from the file
                byte[] encryptedData = File.ReadAllBytes(encryptedDataFilePath);

                // Ask the applet to decrypt the data
                int responseCode;
                byte[] decryptedData = new byte[MAX_SAMPLE_DATA_SIZE];
                jhi.SendAndRecv2(session, LOAD_DATA_CMD, encryptedData, ref decryptedData, out responseCode);
                string txt = UTF32Encoding.UTF8.GetString(decryptedData);
                MessageBox.Show("This is classified information, you will need to enter a username and password to view them", "classified information", MessageBoxButtons.OK, MessageBoxIcon.Information);
                bool v = txt.Contains(textBox2.Text);
                Form1 form1 = new Form1(v, this);
                form1.Show();

                //if (txt.Contains(textBox2.Text))
                //{
                //    MessageBox.Show("This person was in that location", "location found", MessageBoxButtons.OK, MessageBoxIcon.Information);
                //}
                //else
                //{
                //    MessageBox.Show("This person was not in this location", "location was not found", MessageBoxButtons.OK, MessageBoxIcon.Error);
                //}
            }


            //first find if the file exists
            //if the file does not exists show message not exist
            // 1182
            //if the file exists read the file send to applet with cmdid=2
            //this is the 3/ from the telgram
            //            jhi.SendAndRecv2(session, 2, text from the file,  ref recvBuff, out responseCode);

            //jerusalem
            //find if the string exist in the result

            // person = from l in Uri.CheckHostName(item => item.id == textBox1.text)
            //         where l.loction == textBox2.loction
            //         select l).firstOrDefult
            //int index = person.FindIndex(item => item.Id == int.Parse(textBox1.Text));












            //if (index != -1)
            //{

            //    bool v = person[index].locations.Exists(item => item == textBox2.Text);

            //    if (v)
            //    {
            //        MessageBox.Show("This person was in that location", "location found", MessageBoxButtons.OK, MessageBoxIcon.Information);
            //    }
            //    else
            //    {
            //        MessageBox.Show("This person was not in this location", "location was not found", MessageBoxButtons.OK, MessageBoxIcon.Error);
            //    }
            //}
            //else
            //{
            //    MessageBox.Show("Enter id again", "wrong person", MessageBoxButtons.OK, MessageBoxIcon.Error);
            //}
        }

        private void label2_Click(object sender, EventArgs e)
        {

        }
    }
}


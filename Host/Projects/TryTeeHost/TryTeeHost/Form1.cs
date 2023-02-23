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
    public partial class Form1 : Form
    {
        public string encryptedDataFilePath = "";
        public string encryptedDataFolder = "C:\\Users\\tam05\\My Documents\\Visual Studio 2015\\Projects\\Data";
        private const int MAX_SAMPLE_DATA_SIZE = 4096;
        int LOAD_DATA_CMD = 2;
        public bool wasThere { get; set; }
        public MainForm m;
        Jhi jhi = Jhi.Instance;
        JhiSession session;

        public Form1(bool wasThere1, MainForm m1)
        {
            InitializeComponent();
            wasThere = wasThere1;
            label1.Text = "Enter your username";
            label2.Text = "Enter your password";
            button1.Text = "Log In";
            m = m1;
        }



        private void textBox2_TextChanged(object sender, EventArgs e)
        {

        }

        private void label1_Click(object sender, EventArgs e)
        {

        }

        private void button1_Click(object sender, EventArgs e)
        {

            //first find if the file exists




            // Read the encrypted data from the file

            if (m.Chack(textBox2.Text, textBox1.Text))
            {

                if (wasThere)
                {
                    MessageBox.Show("This person was in this location", "location found", MessageBoxButtons.OK, MessageBoxIcon.Information);


                }
                else
                {
                    MessageBox.Show("This person was not in this location", "location was not found", MessageBoxButtons.OK, MessageBoxIcon.Error);

                }
            }
            else
            {
                MessageBox.Show("Wrong password", "Login failure", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }




        }
    }



}













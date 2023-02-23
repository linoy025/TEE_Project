using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;

namespace TryTeeHost
{
    //public enum wasThere
    //{
    //    was,
    //    wasNot
    //}
    public enum isSick
    {
        sick,
        notSick
    }
    public class Person
    {
       // public BackgroundWorker backgroundWorker;

        public string Id { get; set; }


        

       // public wasThere WasThere { get; set; }

        public isSick IsSick { get; set; }

        private DateTime startingTimeFromValidation;
        public string locations { get; set; }
       

        public void Sick()
        {
            IsSick = isSick.sick;
            startingTimeFromValidation = DateTime.Now;
            //backgroundWorker = new BackgroundWorker();
            //backgroundWorker.DoWork += 
        }

        public void CheckSick()
        {
            TimeSpan timePast = DateTime.Now - startingTimeFromValidation;
            if (timePast.Days >= 10 && IsSick == isSick.sick)
            {
                IsSick = isSick.notSick;
            }
        }


    }
}

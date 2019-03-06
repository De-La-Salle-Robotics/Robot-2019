namespace FRCDashboard
{
    partial class FRCDashboard
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            System.Windows.Forms.DataVisualization.Charting.ChartArea chartArea1 = new System.Windows.Forms.DataVisualization.Charting.ChartArea();
            System.Windows.Forms.DataVisualization.Charting.Series series1 = new System.Windows.Forms.DataVisualization.Charting.Series();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FRCDashboard));
            this.pictureBox1 = new System.Windows.Forms.PictureBox();
            this.timer1 = new System.Windows.Forms.Timer(this.components);
            this.splitContainer1 = new System.Windows.Forms.SplitContainer();
            this.lblTargetAngle = new System.Windows.Forms.Label();
            this.lblTargetDistance = new System.Windows.Forms.Label();
            this.chrtBezierDisplay = new System.Windows.Forms.DataVisualization.Charting.Chart();
            this.lblRaspPiAddress = new System.Windows.Forms.Label();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).BeginInit();
            this.splitContainer1.Panel1.SuspendLayout();
            this.splitContainer1.Panel2.SuspendLayout();
            this.splitContainer1.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.chrtBezierDisplay)).BeginInit();
            this.SuspendLayout();
            // 
            // pictureBox1
            // 
            this.pictureBox1.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.pictureBox1.Location = new System.Drawing.Point(0, 84);
            this.pictureBox1.Name = "pictureBox1";
            this.pictureBox1.Size = new System.Drawing.Size(872, 463);
            this.pictureBox1.TabIndex = 0;
            this.pictureBox1.TabStop = false;
            // 
            // timer1
            // 
            this.timer1.Enabled = true;
            this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
            // 
            // splitContainer1
            // 
            this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.splitContainer1.Location = new System.Drawing.Point(0, 0);
            this.splitContainer1.Name = "splitContainer1";
            // 
            // splitContainer1.Panel1
            // 
            this.splitContainer1.Panel1.Controls.Add(this.lblRaspPiAddress);
            this.splitContainer1.Panel1.Controls.Add(this.lblTargetAngle);
            this.splitContainer1.Panel1.Controls.Add(this.lblTargetDistance);
            this.splitContainer1.Panel1.Controls.Add(this.pictureBox1);
            // 
            // splitContainer1.Panel2
            // 
            this.splitContainer1.Panel2.Controls.Add(this.chrtBezierDisplay);
            this.splitContainer1.Size = new System.Drawing.Size(1143, 547);
            this.splitContainer1.SplitterDistance = 871;
            this.splitContainer1.TabIndex = 2;
            // 
            // lblTargetAngle
            // 
            this.lblTargetAngle.AutoSize = true;
            this.lblTargetAngle.Location = new System.Drawing.Point(3, 26);
            this.lblTargetAngle.Name = "lblTargetAngle";
            this.lblTargetAngle.Size = new System.Drawing.Size(71, 13);
            this.lblTargetAngle.TabIndex = 2;
            this.lblTargetAngle.Text = "Target Angle:";
            // 
            // lblTargetDistance
            // 
            this.lblTargetDistance.AutoSize = true;
            this.lblTargetDistance.Location = new System.Drawing.Point(3, 9);
            this.lblTargetDistance.Name = "lblTargetDistance";
            this.lblTargetDistance.Size = new System.Drawing.Size(86, 13);
            this.lblTargetDistance.TabIndex = 1;
            this.lblTargetDistance.Text = "Target Distance:";
            // 
            // chrtBezierDisplay
            // 
            this.chrtBezierDisplay.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            chartArea1.Name = "ChartArea1";
            this.chrtBezierDisplay.ChartAreas.Add(chartArea1);
            this.chrtBezierDisplay.Location = new System.Drawing.Point(3, 3);
            this.chrtBezierDisplay.Name = "chrtBezierDisplay";
            series1.ChartArea = "ChartArea1";
            series1.ChartType = System.Windows.Forms.DataVisualization.Charting.SeriesChartType.Point;
            series1.Name = "BezierCurvePoints";
            series1.XValueType = System.Windows.Forms.DataVisualization.Charting.ChartValueType.Double;
            this.chrtBezierDisplay.Series.Add(series1);
            this.chrtBezierDisplay.Size = new System.Drawing.Size(253, 532);
            this.chrtBezierDisplay.TabIndex = 0;
            this.chrtBezierDisplay.Text = "chart1";
            // 
            // lblRaspPiAddress
            // 
            this.lblRaspPiAddress.AutoSize = true;
            this.lblRaspPiAddress.Location = new System.Drawing.Point(3, 68);
            this.lblRaspPiAddress.Name = "lblRaspPiAddress";
            this.lblRaspPiAddress.Size = new System.Drawing.Size(114, 13);
            this.lblRaspPiAddress.TabIndex = 3;
            this.lblRaspPiAddress.Text = "Raspberry Pi Address: ";
            // 
            // FRCDashboard
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1143, 547);
            this.Controls.Add(this.splitContainer1);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Name = "FRCDashboard";
            this.Text = "7762 Custom Dashboard";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.Form1_FormClosing);
            this.Load += new System.EventHandler(this.Form1_Load);
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).EndInit();
            this.splitContainer1.Panel1.ResumeLayout(false);
            this.splitContainer1.Panel1.PerformLayout();
            this.splitContainer1.Panel2.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).EndInit();
            this.splitContainer1.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.chrtBezierDisplay)).EndInit();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.PictureBox pictureBox1;
        private System.Windows.Forms.Timer timer1;
        private System.Windows.Forms.SplitContainer splitContainer1;
        private System.Windows.Forms.DataVisualization.Charting.Chart chrtBezierDisplay;
        private System.Windows.Forms.Label lblTargetAngle;
        private System.Windows.Forms.Label lblTargetDistance;
        private System.Windows.Forms.Label lblRaspPiAddress;
    }
}


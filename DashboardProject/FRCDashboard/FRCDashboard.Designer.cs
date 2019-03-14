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
            this.tabControl1 = new System.Windows.Forms.TabControl();
            this.tabPage1 = new System.Windows.Forms.TabPage();
            this.tabPage2 = new System.Windows.Forms.TabPage();
            this.grdConnectionProperties = new System.Windows.Forms.PropertyGrid();
            this.grdRio = new System.Windows.Forms.PropertyGrid();
            this.grdRaspPi = new System.Windows.Forms.PropertyGrid();
            this.chrtBezierDisplay = new System.Windows.Forms.DataVisualization.Charting.Chart();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).BeginInit();
            this.splitContainer1.Panel1.SuspendLayout();
            this.splitContainer1.Panel2.SuspendLayout();
            this.splitContainer1.SuspendLayout();
            this.tabControl1.SuspendLayout();
            this.tabPage1.SuspendLayout();
            this.tabPage2.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.chrtBezierDisplay)).BeginInit();
            this.SuspendLayout();
            // 
            // pictureBox1
            // 
            this.pictureBox1.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.pictureBox1.Location = new System.Drawing.Point(6, 6);
            this.pictureBox1.Name = "pictureBox1";
            this.pictureBox1.Size = new System.Drawing.Size(919, 498);
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
            this.splitContainer1.Panel1.Controls.Add(this.tabControl1);
            // 
            // splitContainer1.Panel2
            // 
            this.splitContainer1.Panel2.Controls.Add(this.grdRio);
            this.splitContainer1.Panel2.Controls.Add(this.grdRaspPi);
            this.splitContainer1.Panel2.Controls.Add(this.chrtBezierDisplay);
            this.splitContainer1.Size = new System.Drawing.Size(1231, 548);
            this.splitContainer1.SplitterDistance = 938;
            this.splitContainer1.TabIndex = 2;
            // 
            // tabControl1
            // 
            this.tabControl1.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.tabControl1.Controls.Add(this.tabPage1);
            this.tabControl1.Controls.Add(this.tabPage2);
            this.tabControl1.Location = new System.Drawing.Point(3, 3);
            this.tabControl1.Name = "tabControl1";
            this.tabControl1.SelectedIndex = 0;
            this.tabControl1.Size = new System.Drawing.Size(936, 533);
            this.tabControl1.TabIndex = 6;
            // 
            // tabPage1
            // 
            this.tabPage1.Controls.Add(this.pictureBox1);
            this.tabPage1.Location = new System.Drawing.Point(4, 22);
            this.tabPage1.Name = "tabPage1";
            this.tabPage1.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage1.Size = new System.Drawing.Size(928, 507);
            this.tabPage1.TabIndex = 0;
            this.tabPage1.Text = "Camera Stream";
            this.tabPage1.UseVisualStyleBackColor = true;
            // 
            // tabPage2
            // 
            this.tabPage2.Controls.Add(this.grdConnectionProperties);
            this.tabPage2.Location = new System.Drawing.Point(4, 22);
            this.tabPage2.Name = "tabPage2";
            this.tabPage2.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage2.Size = new System.Drawing.Size(928, 507);
            this.tabPage2.TabIndex = 1;
            this.tabPage2.Text = "Connection Info";
            this.tabPage2.UseVisualStyleBackColor = true;
            // 
            // grdConnectionProperties
            // 
            this.grdConnectionProperties.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.grdConnectionProperties.HelpVisible = false;
            this.grdConnectionProperties.Location = new System.Drawing.Point(6, 6);
            this.grdConnectionProperties.Name = "grdConnectionProperties";
            this.grdConnectionProperties.PropertySort = System.Windows.Forms.PropertySort.NoSort;
            this.grdConnectionProperties.RightToLeft = System.Windows.Forms.RightToLeft.No;
            this.grdConnectionProperties.Size = new System.Drawing.Size(916, 495);
            this.grdConnectionProperties.TabIndex = 6;
            this.grdConnectionProperties.ToolbarVisible = false;
            this.grdConnectionProperties.Enter += new System.EventHandler(this.grdConnectionProperties_Enter);
            this.grdConnectionProperties.Leave += new System.EventHandler(this.grdConnectionProperties_Leave);
            this.grdConnectionProperties.PreviewKeyDown += new System.Windows.Forms.PreviewKeyDownEventHandler(this.grdConnectionProperties_PreviewKeyDown);
            // 
            // grdRio
            // 
            this.grdRio.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.grdRio.HelpVisible = false;
            this.grdRio.Location = new System.Drawing.Point(3, 128);
            this.grdRio.Name = "grdRio";
            this.grdRio.PropertySort = System.Windows.Forms.PropertySort.NoSort;
            this.grdRio.Size = new System.Drawing.Size(274, 160);
            this.grdRio.TabIndex = 4;
            this.grdRio.ToolbarVisible = false;
            // 
            // grdRaspPi
            // 
            this.grdRaspPi.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.grdRaspPi.HelpVisible = false;
            this.grdRaspPi.Location = new System.Drawing.Point(3, 3);
            this.grdRaspPi.Name = "grdRaspPi";
            this.grdRaspPi.PropertySort = System.Windows.Forms.PropertySort.NoSort;
            this.grdRaspPi.Size = new System.Drawing.Size(274, 119);
            this.grdRaspPi.TabIndex = 3;
            this.grdRaspPi.ToolbarVisible = false;
            // 
            // chrtBezierDisplay
            // 
            this.chrtBezierDisplay.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            chartArea1.Name = "ChartArea1";
            this.chrtBezierDisplay.ChartAreas.Add(chartArea1);
            this.chrtBezierDisplay.Location = new System.Drawing.Point(3, 294);
            this.chrtBezierDisplay.Name = "chrtBezierDisplay";
            series1.ChartArea = "ChartArea1";
            series1.ChartType = System.Windows.Forms.DataVisualization.Charting.SeriesChartType.Point;
            series1.Name = "BezierCurvePoints";
            series1.XValueType = System.Windows.Forms.DataVisualization.Charting.ChartValueType.Double;
            this.chrtBezierDisplay.Series.Add(series1);
            this.chrtBezierDisplay.Size = new System.Drawing.Size(274, 242);
            this.chrtBezierDisplay.TabIndex = 0;
            this.chrtBezierDisplay.Text = "chart1";
            // 
            // FRCDashboard
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackColor = System.Drawing.SystemColors.Control;
            this.ClientSize = new System.Drawing.Size(1231, 548);
            this.Controls.Add(this.splitContainer1);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Name = "FRCDashboard";
            this.Text = "7762 Custom Dashboard";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.Form1_FormClosing);
            this.Load += new System.EventHandler(this.Form1_Load);
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).EndInit();
            this.splitContainer1.Panel1.ResumeLayout(false);
            this.splitContainer1.Panel2.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).EndInit();
            this.splitContainer1.ResumeLayout(false);
            this.tabControl1.ResumeLayout(false);
            this.tabPage1.ResumeLayout(false);
            this.tabPage2.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.chrtBezierDisplay)).EndInit();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.PictureBox pictureBox1;
        private System.Windows.Forms.Timer timer1;
        private System.Windows.Forms.SplitContainer splitContainer1;
        private System.Windows.Forms.DataVisualization.Charting.Chart chrtBezierDisplay;
        private System.Windows.Forms.TabControl tabControl1;
        private System.Windows.Forms.TabPage tabPage1;
        private System.Windows.Forms.TabPage tabPage2;
        private System.Windows.Forms.PropertyGrid grdConnectionProperties;
        private System.Windows.Forms.PropertyGrid grdRaspPi;
        private System.Windows.Forms.PropertyGrid grdRio;
    }
}


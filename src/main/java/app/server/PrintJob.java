package app.server;

public class PrintJob {
        private int id;
        private String filename;
        private String printer;

        public PrintJob(int id, String filename, String printer) {
            this.id = id;
            this.filename = filename;
            this.printer = printer;
        }

        public int getId() {
            return id;
        }

        public String getFilename() {
            return filename;
        }

        public String getPrinter() {
            return printer;
        }

        @Override
        public String toString() {
            return "Job ID: " + id + ", File: " + filename + ", Printer: " + printer;
        }
    }
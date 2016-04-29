package com.example.tomipc.foodzye.model;

    public class DrawerItem {
        private String title;
        private int icon;
        public DrawerItem() {
        }

        public DrawerItem(String title, int icon) {
            this.title = title;
            this.icon = icon;
        }
        public DrawerItem(String title) {
            this.title = title;
        }

        public String getTitle() {
            return this.title;
        }

        public int getIcon() {
            return this.icon;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }
}


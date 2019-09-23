package com.qwant.liberty;

class SuggestItem {
    enum Type {
        QWANT_SUGGEST,
        HISTORY
    }

    Type type;
    String display_text;

    SuggestItem(Type type, String display_text) {
        this.type = type;
        this.display_text = display_text;
    }
}

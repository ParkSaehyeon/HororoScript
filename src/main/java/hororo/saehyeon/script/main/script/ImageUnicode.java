package hororo.saehyeon.script.main.script;

public enum ImageUnicode {

    MIYA_DEFAULT('\uE001'),
    AIKA_DEFAULT('\uE002');

    final char imageUnicode;

    ImageUnicode(char imageUnicode) {
        this.imageUnicode = imageUnicode;
    }

    public char getImageUnicode() {
        return imageUnicode;
    }
}

package cz.larpovadatabaze.common.services.builders;

import cz.larpovadatabaze.common.entities.*;

public class MasqueradeEntities {
    public CsldUser administrator;
    public CsldUser editor;
    public CsldUser user;

    public CsldGroup nosferatu;
    public CsldGroup toreador;

    public Game firstMasquerade;
    public Game secondMasquerade;

    public Label vampire;
    public Label dramatic;
    public Label emotional;
    public Label chamber;

    public Comment editorComment;
    public Comment userComment;

    public MasqueradeEntities(CsldUser administrator, CsldUser editor, CsldUser user,
                              CsldGroup nosferatu, CsldGroup toreador, Game firstMasquerade,
                              Game secondMasquerade, Label vampire, Label dramatic,
                              Label emotional, Label chamber,
                              Comment editorComment, Comment userComment) {
        this.administrator = administrator;
        this.editor = editor;
        this.user = user;
        this.nosferatu = nosferatu;
        this.toreador = toreador;
        this.firstMasquerade = firstMasquerade;
        this.secondMasquerade = secondMasquerade;
        this.vampire = vampire;
        this.dramatic = dramatic;
        this.emotional = emotional;
        this.chamber = chamber;
        this.editorComment = editorComment;
        this.userComment = userComment;
    }
}

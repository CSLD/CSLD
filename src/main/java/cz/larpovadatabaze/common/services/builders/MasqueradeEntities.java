package cz.larpovadatabaze.common.services.builders;

import cz.larpovadatabaze.common.entities.*;

public class MasqueradeEntities {
    public final Game wrongMasquerade;
    public final CsldUser administrator;
    public final CsldUser editor;
    public final CsldUser user;

    public final CsldGroup nosferatu;
    public final CsldGroup toreador;

    public final Game firstMasquerade;
    public final Game secondMasquerade;
    public final Game bestMasquerade;

    public final Label vampire;
    public final Label dramatic;
    public final Label emotional;
    public final Label chamber;

    public final Comment editorComment;
    public final Comment userComment;

    public final Rating userRatedBest;
    public final Rating userRatedSecond;

    public final Upvote editorUserComment;

    public MasqueradeEntities(CsldUser administrator, CsldUser editor, CsldUser user,
                              CsldGroup nosferatu, CsldGroup toreador,
                              Game firstMasquerade, Game secondMasquerade, Game bestMasquerade, Game wrongMasquerade,
                              Label vampire, Label dramatic, Label emotional, Label chamber,
                              Comment editorComment, Comment userComment, Rating userRatedBest, Rating userRatedSecond, Upvote editorUserComment) {
        this.administrator = administrator;
        this.editor = editor;
        this.user = user;
        this.nosferatu = nosferatu;
        this.toreador = toreador;
        this.firstMasquerade = firstMasquerade;
        this.secondMasquerade = secondMasquerade;
        this.bestMasquerade = bestMasquerade;
        this.wrongMasquerade = wrongMasquerade;
        this.vampire = vampire;
        this.dramatic = dramatic;
        this.emotional = emotional;
        this.chamber = chamber;
        this.editorComment = editorComment;
        this.userComment = userComment;
        this.userRatedBest = userRatedBest;
        this.userRatedSecond = userRatedSecond;
        this.editorUserComment = editorUserComment;
    }
}

/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer.downloads;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import sancho.core.ICore;
import sancho.core.Sancho;
import sancho.model.mldonkey.File;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.view.utility.SResources;
import sancho.view.utility.Spinner;
import sancho.view.utility.WidgetFactory;

public class EditMP3TagsDialog extends Dialog {
  private ICore core;
  private File file;
  private Text artist;
  private Text album;
  private Text title;
  private Text comment;
  private Spinner trackNumber;
  private Text year;
  private Combo genre;

  private static final String[] genres = {"Blues", "Classic Rock", "Country", "Dance", "Disco", "Funk",
      "Grunge", "Hip-Hop", "Jazz", "Metal", "New Age", "Oldies", "Other", "Pop", "R&B", "Rap", "Reggae",
      "Rock", "Techno", "Industrial", "Alternative", "Ska", "Death Metal", "Pranks", "Soundtrack",
      "Euro-Techno", "Ambient", "Trip-Hop", "Vocal", "Jazz+Funk", "Fusion", "Trance", "Classical",
      "Instrumental", "Acid", "House", "Game", "Sound Clip", "Gospel", "Noise", "Alt. Rock", "Bass", "Soul",
      "Punk", "Space", "Meditative", "Instrumental Pop", "Instrumental Rock", "Ethnic", "Gothic", "Darkwave",
      "Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance", "Dream", "Southern Rock", "Comedy", "Cult",
      "Gangsta Rap", "Top 40", "Christian Rap", "Pop/Funk", "Jungle", "Native American", "Cabaret",
      "New Wave", "Psychedelic", "Rave", "Showtunes", "Trailer", "Lo-Fi", "Tribal", "Acid Punk", "Acid Jazz",
      "Polka", "Retro", "Musical", "Rock & Roll", "Hard Rock", "Folk", "Folk/Rock", "National Folk", "Swing",
      "Fast-Fusion", "Bebob", "Latin", "Revival", "Celtic", "Bluegrass", "Avantgarde", "Gothic Rock",
      "Progressive Rock", "Psychedelic Rock", "Symphonic Rock", "Slow Rock", "Big Band", "Chorus",
      "Easy Listening", "Acoustic", "Humour", "Speech", "Chanson", "Opera", "Chamber Music", "Sonata",
      "Symphony", "Booty Bass", "Primus", "Porn Groove", "Satire", "Slow Jam", "Club", "Tango", "Samba",
      "Folklore", "Ballad", "Power Ballad", "Rhythmic Soul", "Freestyle", "Duet", "Punk Rock", "Drum Solo",
      "A Cappella", "Euro-House", "Dance Hall", "Goa", "Drum & Bass", "Club-House", "Hardcore", "Terror",
      "Indie", "BritPop", "Negerpunk", "Polsk Punk", "Beat", "Christian Gangsta Rap", "Heavy Metal",
      "Black Metal", "Crossover", "Contemporary Christian", "Christian Rock", "Merengue", "Salsa",
      "Thrash Metal", "Anime", "JPop", "Synthpop"};

  public EditMP3TagsDialog(Shell parentShell, ICore core, File file) {
    super(parentShell);
    this.core = core;
    this.file = file;
  }

  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setImage(SResources.getImage("ProgramIcon"));
    newShell.setText(SResources.getString("Edit MP3 Tags"));
  }

  protected Control createDialogArea(Composite parent) {
    Composite composite = (Composite) super.createDialogArea(parent);
    composite.setLayout(WidgetFactory.createGridLayout(2, 5, 5, 10, 5, false));

    createLabel(composite, "Artist:");
    artist = createText(composite, file.getFormat().getMP3Artist());

    createLabel(composite, "Album:");
    album = createText(composite, file.getFormat().getMP3Album());

    createLabel(composite, "Title:");
    title = createText(composite, file.getFormat().getMP3Title());

    createLabel(composite, "Comment:");
    comment = createText(composite, file.getFormat().getMP3Comment());

    createLabel(composite, "Track number:");
    trackNumber = new Spinner(composite, SWT.NONE);
    trackNumber.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    trackNumber.setSelection(file.getFormat().getMP3TrackNum());

    createLabel(composite, "Year");
    year = createText(composite, file.getFormat().getMP3Year());

    createLabel(composite, "Genre:");
    genre = new Combo(composite, SWT.BORDER);
    genre.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    for (int i = 0; i < genres.length; i++)
      genre.add(genres[i]);
    genre.select(file.getFormat().getMP3Genre());
    
    return composite;
  }

  protected void createLabel(Composite composite, String string) {
    Label label = new Label(composite, SWT.NONE);
    label.setText(string);
    label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
  }

  protected Text createText(Composite composite, String string) {
    Text text = new Text(composite, SWT.BORDER);
    text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    text.setText(string);
    return text;
  }

  protected Control createButtonBar(Composite parent) {
    Composite buttonComposite = new Composite(parent, SWT.NONE);
    buttonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    buttonComposite.setLayout(WidgetFactory.createGridLayout(2, 5, 5, 5, 0, false));

    Button okButton = new Button(buttonComposite, SWT.NONE);
    okButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    okButton.setText(SResources.getString("b.ok"));
    okButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        Object[] oArray = new Object[8];
        oArray[0] = new Integer(file.getId());
        oArray[1] = title.getText();
        oArray[2] = artist.getText();
        oArray[3] = album.getText();
        oArray[4] = year.getText();
        oArray[5] = comment.getText();
        oArray[6] = new Integer(trackNumber.getSelection());
        oArray[7] = new Integer(genre.getSelectionIndex());
        Sancho.send(OpCodes.S_MODIFY_MP3_TAGS, oArray);
        close();
      }
    });

    Button cancelButton = new Button(buttonComposite, SWT.NONE);
    cancelButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    cancelButton.setText(SResources.getString("b.cancel"));
    cancelButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        close();
      }
    });

    return buttonComposite;
  }
}
package uk.kihira.tails.client.gui;

import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.Tails;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LibraryPanel extends Panel<GuiEditor> implements IListCallback<LibraryListEntry> {
    private static final LibrarySorter SORTER = new LibrarySorter();
    private static final int ALL_BUTTON = 0;

    private GuiList<LibraryListEntry> list;
    private GuiTextField searchField;

    public LibraryPanel(GuiEditor parent, int left, int top, int width, int height) {
        super(parent, left, top, width, height);
    }

    @Override
    public void initGui() {
        initList();

        buttonList.add(new GuiButtonExt(ALL_BUTTON, 3, height - 18, width - 6, 15, I18n.format("gui.button.all")));
        searchField = new GuiTextField(1, fontRenderer, 5, height - 31, width - 10, 10);
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        zLevel = -100;
        drawGradientRect(0, 0, width, height, GuiEditor.DARK_GREY, GuiEditor.DARK_GREY);

        searchField.drawTextBox();
        list.drawScreen(mouseX, mouseY, partialTicks);

        zLevel = 0;
        Minecraft.getMinecraft().renderEngine.bindTexture(GuiIconButton.ICONS_TEXTURES);
        GlStateManager.pushMatrix();
        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.translate(width - 16, height - 32, 0);
        GlStateManager.scale(.75f, .75f, 0f);
        drawTexturedModalRect(0, 0, 160, 0, 16, 16);
        GlStateManager.popMatrix();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == ALL_BUTTON) {
            // todo?
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        list.mouseClicked(mouseX, mouseY, mouseButton);
        searchField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        list.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public void handleMouseInput() {
        list.handleMouseInput();
    }

    @Override
    public void keyTyped(char key, int keyCode) {
        searchField.textboxKeyTyped(key, keyCode);
        if (searchField.getVisible() && searchField.isFocused()) {
            List<LibraryListEntry> newEntries = filterListEntries(searchField.getText().toLowerCase());
            newEntries.add(0, new LibraryListEntry.NewLibraryListEntry(this, null));
            list.getEntries().clear();
            list.getEntries().addAll(newEntries);
        }
        super.keyTyped(key, keyCode);
    }

    @Override
    public boolean onEntrySelected(GuiList guiList, int index, LibraryListEntry entry) {
        if (!(entry instanceof LibraryListEntry.NewLibraryListEntry)) {
            parent.libraryInfoPanel.setEntry(entry);
            parent.setOutfit(entry.data.outfit);
        }
        return true;
    }

    public void initList() {
        List<LibraryListEntry> libraryEntries = new ArrayList<>();
        for (LibraryEntryData data : Tails.proxy.getLibraryManager().libraryEntries) {
            libraryEntries.add(new LibraryListEntry(data));
        }

        //Add in new entry creation
        libraryEntries.add(0, new LibraryListEntry.NewLibraryListEntry(this, null));

        libraryEntries.sort(SORTER);

        list = new GuiList<>(this, width, height - 34, 0, height - 34, 50, libraryEntries);
    }

    public void addSelectedEntry(LibraryListEntry entry) {
        list.getEntries().add(entry);
        list.setCurrentIndex(list.getEntries().size() - 1);
        parent.libraryInfoPanel.setEntry(entry);
    }

    public void removeEntry(LibraryListEntry entry) {
        Tails.proxy.getLibraryManager().removeEntry(entry.data);
        list.getEntries().remove(entry);
    }

    private List<LibraryListEntry> filterListEntries(String filter) {
        ArrayList<LibraryListEntry> filteredEntries = new ArrayList<>();
        List<LibraryListEntry> entries = new ArrayList<>();

        for (LibraryEntryData data : Tails.proxy.getLibraryManager().libraryEntries) {
            entries.add(new LibraryListEntry(data));
        }

        for (LibraryListEntry entry : entries) {
            if (entry instanceof LibraryListEntry.NewLibraryListEntry || entry.data.entryName.toLowerCase().contains(filter)) {
                filteredEntries.add(entry);
            }
        }
        return filteredEntries;
    }

    @Override
    public void onGuiClosed() {
        Tails.proxy.getLibraryManager().removeRemoteEntries();
        super.onGuiClosed();
    }

    private static class LibrarySorter implements Comparator<LibraryListEntry> {

        @Override
        public int compare(LibraryListEntry entry1, LibraryListEntry entry2) {
            if (entry1.equals(entry2)) {
                return 0;
            }

            // Always ensure new library entry button is at the top
            if (entry1 instanceof LibraryListEntry.NewLibraryListEntry) {
                return Integer.MIN_VALUE;
            } else if (entry2 instanceof LibraryListEntry.NewLibraryListEntry) {
                return Integer.MAX_VALUE;
            }

            //Put favourites at the top
            if (entry1.data.favourite && !entry2.data.favourite) {
                return -1;
            } else if (!entry1.data.favourite && entry2.data.favourite) {
                return 1;
            }

            return 0;
        }
    }
}

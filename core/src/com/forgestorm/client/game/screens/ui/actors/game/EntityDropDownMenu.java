package com.forgestorm.client.game.screens.ui.actors.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.screens.ui.actors.LeftAlignTextButton;
import com.forgestorm.client.game.screens.ui.actors.dev.entity.EntityEditor;
import com.forgestorm.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.forgestorm.client.game.screens.ui.actors.game.chat.ChatChannelType;
import com.forgestorm.client.game.world.entities.AiEntity;
import com.forgestorm.client.game.world.entities.Entity;
import com.forgestorm.client.game.world.entities.EntityInteract;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.EntityType;
import com.forgestorm.client.game.world.entities.ItemStackDrop;
import com.forgestorm.client.game.world.entities.MovingEntity;
import com.forgestorm.client.game.world.entities.NPC;
import com.forgestorm.client.game.world.entities.Player;
import com.forgestorm.client.game.world.entities.PlayerClient;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.network.game.packet.out.InspectPlayerPacketOut;
import com.forgestorm.shared.game.world.item.ItemStack;
import com.forgestorm.shared.game.world.item.ItemStackType;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.ArrayList;
import java.util.List;

public class EntityDropDownMenu extends HideableVisWindow implements Buildable {

    private final EntityDropDownMenu dropDownMenu;
    private StageHandler stageHandler;
    private final VisTable dropDownTable = new VisTable();

    private final List<Entity> entityArray = new ArrayList<Entity>();

    public EntityDropDownMenu() {
        super("Choose Option");
        this.dropDownMenu = this;
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;
        add(dropDownTable).grow();

        addListener(new ForceCloseWindowListener() {
            @Override
            public void handleClose() {
                cleanUpDropDownMenu(true);
            }
        });

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                centerWindow();
            }
        });

        setVisible(false);
        return this;
    }

    public void toggleMenu(List<Entity> entityList, float x, float y) {
        cleanUpDropDownMenu(false);
        setPosition(x, y);

        for (Entity entity : entityList) {
            dropDownTable.add(new EditorMenuEntry(entity)).expand().fill().row();

            // Adds players, monsters, and npcs;
            dropDownTable.add(new MenuEntry(entity)).expand().fill().row();

            entityArray.add(entity);
        }

        addWalkHereButton(dropDownTable, entityList.get(0).getCurrentMapLocation());
        addCancelButton(dropDownTable);

        pack();
        ActorUtil.fadeInWindow(dropDownMenu);
        toFront();
    }

    private void addWalkHereButton(VisTable visTable, final Location toLocation) {
        LeftAlignTextButton walkHereButton = new LeftAlignTextButton("Walk Here");
        visTable.add(walkHereButton).expand().fill().row();

        walkHereButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);
                ClientMain.getInstance().getEntityTracker().walkTo(toLocation.getX(), toLocation.getY(), toLocation.getZ(), false);
                cleanUpDropDownMenu(true);
            }
        });
    }

    private void addCancelButton(VisTable visTable) {
        LeftAlignTextButton cancelButton = new LeftAlignTextButton("Cancel");
        visTable.add(cancelButton).expand().fill().row();

        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);
                cleanUpDropDownMenu(true);
            }
        });
    }

    private void cleanUpDropDownMenu(boolean closeWindow) {
        if (closeWindow) ActorUtil.fadeOutWindow(dropDownMenu);
        dropDownTable.clearListeners();
        dropDownTable.clearChildren();
        entityArray.clear();
    }

    public void closeDropDownMenu(EntityType entityType, short entityId) {
        boolean clean = false;
        for (Entity entity : entityArray) {
            if (entity.getEntityType() != entityType) continue;
            if (entity.getServerEntityID() == entityId) {
                clean = true;
                break;
            }
        }
        if (clean) cleanUpDropDownMenu(true);
    }

    class EditorMenuEntry extends VisTable {

        private final Entity clickedEntity;

        EditorMenuEntry(Entity clickedEntity) {
            this.clickedEntity = clickedEntity;

            addEditEntityButton();
        }

        private void addEditEntityButton() {
            if (clickedEntity.getEntityType() == EntityType.PLAYER || clickedEntity.getEntityType() == EntityType.CLIENT_PLAYER) return;
            if (!ClientMain.getInstance().isAdmin() && !ClientMain.getInstance().isContentDeveloper()) return;
            if (clickedEntity.getEntityType() == EntityType.ITEM_STACK) {
                // If this ItemStackDrop spawned from an Entity Kill, then don't allow editor button
                if (((ItemStackDrop) clickedEntity).isSpawnedFromDropTable()) return;
            }

            LeftAlignTextButton editEntityButton = new LeftAlignTextButton("Edit " + clickedEntity.getEntityName());
            editEntityButton.setColor(Color.YELLOW);
            add(editEntityButton).expand().fill().row();

            editEntityButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);

                    EntityEditor entityEditor = stageHandler.getEntityEditor();
                    if (clickedEntity.getEntityType() == EntityType.MONSTER) {
                        entityEditor.getTabbedPane().switchTab(entityEditor.getMonsterTab());
                        entityEditor.getMonsterTab().loadAiEntity((AiEntity) clickedEntity);
                    } else if (clickedEntity.getEntityType() == EntityType.NPC) {
                        entityEditor.getTabbedPane().switchTab(entityEditor.getNpcTab());
                        entityEditor.getNpcTab().loadAiEntity((AiEntity) clickedEntity);
                    } else if (clickedEntity.getEntityType() == EntityType.ITEM_STACK) {
                        entityEditor.getTabbedPane().switchTab(entityEditor.getItemStackDropTab());
                        entityEditor.getItemStackDropTab().loadEntity((ItemStackDrop) clickedEntity);
                    }
                    ActorUtil.fadeInWindow(entityEditor);

                    stageHandler.getChatWindow().appendChatMessage(ChatChannelType.GENERAL, "[YELLOW]Editing " + clickedEntity.getEntityName() + ".");
                    cleanUpDropDownMenu(true);
                }
            });
        }
    }

    class MenuEntry extends VisTable {

        private MovingEntity clickedMovingEntity;
        private final PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        private final String entityName;

        MenuEntry(Entity clickedEntity) {

            if (clickedEntity instanceof MovingEntity)
                this.clickedMovingEntity = (MovingEntity) clickedEntity;

            Color nameColor = Color.GRAY;
            if (clickedEntity instanceof AiEntity) {
                nameColor = ((AiEntity) clickedEntity).getAlignment().getDefaultColor();
            } else if (clickedEntity instanceof ItemStackDrop) {
                nameColor = Color.YELLOW;
            }
            entityName = "[#" + nameColor + "]" + clickedEntity.getEntityName();

            if (clickedEntity instanceof MovingEntity) {
                addOpenBankButton();
                addTradeButton();
                addShopButton();
                addTalkButton();
                addInspectPlayerButton();
                addTargetButton();
                addFollowButton();
                addViewProfileButton();
            } else if (clickedEntity instanceof ItemStackDrop) {
                addPickupButton((ItemStackDrop) clickedEntity);
            }
        }

        private void addViewProfileButton() {
            if (!(clickedMovingEntity instanceof Player)) return;

            LeftAlignTextButton openPlayerProfileButton = new LeftAlignTextButton("Open " + entityName + "'s Profile");
            add(openPlayerProfileButton).expand().fill().row();

            openPlayerProfileButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);
                    stageHandler.getPlayerProfileWindow().requestPlayerProfile((Player) clickedMovingEntity);
                    cleanUpDropDownMenu(true);
                }
            });
        }

        private void addPickupButton(final ItemStackDrop itemStackDrop) {
            // This is only used to get data about the ItemStack drop
            ItemStack itemStack = ClientMain.getInstance().getItemStackManager().makeItemStack(itemStackDrop.getItemStackId(), 1);

            LeftAlignTextButton pickupButton;
            if (itemStack.getItemStackType() == ItemStackType.GOLD) {
                pickupButton = new LeftAlignTextButton("Pick up [GOLD]" + entityName);
            } else {
                pickupButton = new LeftAlignTextButton("Pick up " + entityName);
            }

            add(pickupButton).expand().fill().row();

            pickupButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    // Picking up ItemStacks from the ground

                    EntityInteract.pickUpItemStackDrop(itemStackDrop);
                    cleanUpDropDownMenu(true);
                }

            });
        }

        // Talk, OpenBank, Attack, Trade, Shop, Follow, Exit, Walk Here
        private void addTalkButton() {
            if (!(clickedMovingEntity instanceof NPC)) return;

            LeftAlignTextButton talkButton = new LeftAlignTextButton("Talk To " + entityName);
            add(talkButton).expand().fill().row();

            talkButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor) {
                    EntityInteract.talkNPC((NPC) clickedMovingEntity);
                    cleanUpDropDownMenu(true);
                }
            });
        }

        private void addInspectPlayerButton() {
            if (clickedMovingEntity instanceof AiEntity) return;

            LeftAlignTextButton inspectPlayerButton = new LeftAlignTextButton("Inspect " + entityName);
            add(inspectPlayerButton).expand().fill().row();

            inspectPlayerButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);

                    new InspectPlayerPacketOut(clickedMovingEntity.getServerEntityID()).sendPacket();
                    stageHandler.getCharacterInspectionWindow().setPlayerToInspect((Player) clickedMovingEntity);

                    stageHandler.getChatWindow().appendChatMessage(ChatChannelType.GENERAL, "[YELLOW]Inspecting player [GOLD]" + clickedMovingEntity.getEntityName() + "s [YELLOW]equipment.");
                    cleanUpDropDownMenu(true);
                }
            });
        }

        private void addOpenBankButton() {
            if (clickedMovingEntity.getEntityType() == EntityType.CLIENT_PLAYER || clickedMovingEntity.getEntityType() == EntityType.PLAYER)
                return;
            if (!((AiEntity) clickedMovingEntity).isBankKeeper()) return;

            LeftAlignTextButton openBankButton = new LeftAlignTextButton("Open Bank");
            add(openBankButton).fill().row();

            openBankButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor) {
                    EntityInteract.openBank(clickedMovingEntity);
                    cleanUpDropDownMenu(true);
                }
            });
        }

        private void addTargetButton() {
            LeftAlignTextButton attackButton = new LeftAlignTextButton("Target " + entityName);
            add(attackButton).expand().fill().row();

            attackButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);
                    playerClient.setTargetEntity(clickedMovingEntity);
                    cleanUpDropDownMenu(true);
                }
            });
        }

        private void addTradeButton() {
            if (clickedMovingEntity.getEntityType() != EntityType.PLAYER) return;
            LeftAlignTextButton tradeButton = new LeftAlignTextButton("Trade with " + entityName);
            add(tradeButton).expand().fill().row();

            tradeButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    EntityInteract.trade(stageHandler, (Player) clickedMovingEntity);
                    cleanUpDropDownMenu(true);
                }
            });
        }

        private void addShopButton() {
            if (clickedMovingEntity.getEntityType() == EntityType.PLAYER) return;
            if (((AiEntity) clickedMovingEntity).getShopID() < 0) return;
            LeftAlignTextButton shopButton = new LeftAlignTextButton("Open Shop");
            add(shopButton).expand().fill().row();

            shopButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    EntityInteract.openShop((AiEntity) clickedMovingEntity);
                    cleanUpDropDownMenu(true);
                }
            });
        }

        private void addFollowButton() {
            if (clickedMovingEntity instanceof AiEntity && ((AiEntity) clickedMovingEntity).isBankKeeper())
                return;

            LeftAlignTextButton followButton = new LeftAlignTextButton("Follow " + entityName);
            add(followButton).expand().fill().row();

            followButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);
                    ClientMain.getInstance().getEntityTracker().follow(clickedMovingEntity);
                    cleanUpDropDownMenu(true);
                }
            });
        }
    }
}

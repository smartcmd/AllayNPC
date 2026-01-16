package me.daoge.allaynpc.i18n;

import lombok.experimental.UtilityClass;

/**
 * AllayNPC i18n translation keys
 *
 * @author daoge_cmd
 */
@UtilityClass
public class I18nKeys {

    // Plugin lifecycle
    public static final String PLUGIN_LOADING = "allaynpc:plugin.loading";
    public static final String PLUGIN_ENABLING = "allaynpc:plugin.enabling";
    public static final String PLUGIN_ENABLED = "allaynpc:plugin.enabled";
    public static final String PLUGIN_DISABLING = "allaynpc:plugin.disabling";
    public static final String PLUGIN_DISABLED = "allaynpc:plugin.disabled";
    public static final String PLUGIN_RELOADING = "allaynpc:plugin.reloading";
    public static final String PLUGIN_RELOADED = "allaynpc:plugin.reloaded";

    // Directory creation
    public static final String DIRECTORY_SKINS_CREATED = "allaynpc:directory.skins.created";
    public static final String DIRECTORY_CAPES_CREATED = "allaynpc:directory.capes.created";
    public static final String DIRECTORY_DIALOGS_CREATED = "allaynpc:directory.dialogs.created";
    public static final String DIRECTORY_NPCS_CREATED = "allaynpc:directory.npcs.created";
    public static final String DIRECTORY_CREATE_FAILED = "allaynpc:directory.create.failed";

    // Manager messages
    public static final String MANAGER_SKINS_LOADED = "allaynpc:manager.skins.loaded";
    public static final String MANAGER_CAPES_LOADED = "allaynpc:manager.capes.loaded";
    public static final String MANAGER_DIALOGS_LOADED = "allaynpc:manager.dialogs.loaded";
    public static final String MANAGER_NPCS_LOADED = "allaynpc:manager.npcs.loaded";
    public static final String MANAGER_SKINS_RELOADED = "allaynpc:manager.skins.reloaded";
    public static final String MANAGER_CAPES_RELOADED = "allaynpc:manager.capes.reloaded";
    public static final String MANAGER_DIALOGS_RELOADED = "allaynpc:manager.dialogs.reloaded";
    public static final String MANAGER_NPCS_RELOADED = "allaynpc:manager.npcs.reloaded";

    // Command and event registration
    public static final String COMMAND_REGISTERED = "allaynpc:command.registered";
    public static final String COMMAND_DESCRIPTION = "allaynpc:command.description";
    public static final String EVENT_REGISTERED = "allaynpc:event.registered";
    public static final String TASK_STARTED = "allaynpc:task.started";
    public static final String NPC_UPDATE_ERROR = "allaynpc:npc.update.error";

    // Command messages
    public static final String COMMAND_NPC_EXISTS = "allaynpc:command.npc.exists";
    public static final String COMMAND_NPC_NOTFOUND = "allaynpc:command.npc.notfound";
    public static final String COMMAND_NPC_DELETED = "allaynpc:command.npc.deleted";
    public static final String COMMAND_NPC_DELETE_FAILED = "allaynpc:command.npc.delete.failed";
    public static final String COMMAND_NPC_SPAWNED = "allaynpc:command.npc.spawned";
    public static final String COMMAND_NPC_SPAWN_FAILED = "allaynpc:command.npc.spawn.failed";
    public static final String COMMAND_NPC_REMOVED = "allaynpc:command.npc.removed";
    public static final String COMMAND_NPC_NOPOSITION = "allaynpc:command.npc.noposition";
    public static final String COMMAND_WORLD_NOTFOUND = "allaynpc:command.world.notfound";
    public static final String COMMAND_DIMENSION_NOTFOUND = "allaynpc:command.dimension.notfound";
    public static final String COMMAND_TELEPORTED = "allaynpc:command.teleported";
    public static final String COMMAND_RELOADED = "allaynpc:command.reloaded";

    // List command
    public static final String COMMAND_LIST_EMPTY = "allaynpc:command.list.empty";
    public static final String COMMAND_LIST_HEADER = "allaynpc:command.list.header";
    public static final String COMMAND_LIST_SPAWNED = "allaynpc:command.list.spawned";
    public static final String COMMAND_LIST_NOTSPAWNED = "allaynpc:command.list.notspawned";

    // Skins command
    public static final String COMMAND_SKINS_EMPTY = "allaynpc:command.skins.empty";
    public static final String COMMAND_SKINS_HEADER = "allaynpc:command.skins.header";

    // Help command
    public static final String COMMAND_HELP_TITLE = "allaynpc:command.help.title";
    public static final String COMMAND_HELP_CREATE = "allaynpc:command.help.create";
    public static final String COMMAND_HELP_EDIT = "allaynpc:command.help.edit";
    public static final String COMMAND_HELP_DELETE = "allaynpc:command.help.delete";
    public static final String COMMAND_HELP_LIST = "allaynpc:command.help.list";
    public static final String COMMAND_HELP_TP = "allaynpc:command.help.tp";
    public static final String COMMAND_HELP_SPAWN = "allaynpc:command.help.spawn";
    public static final String COMMAND_HELP_REMOVE = "allaynpc:command.help.remove";
    public static final String COMMAND_HELP_SKINS = "allaynpc:command.help.skins";
    public static final String COMMAND_HELP_RELOAD = "allaynpc:command.help.reload";

    // Form - Create
    public static final String FORM_CREATE_TITLE = "allaynpc:form.create.title";
    public static final String FORM_CREATE_DISPLAYNAME = "allaynpc:form.create.displayname";
    public static final String FORM_CREATE_DISPLAYNAME_PLACEHOLDER = "allaynpc:form.create.displayname.placeholder";
    public static final String FORM_CREATE_ALWAYSSHOWNAME = "allaynpc:form.create.alwaysshowname";
    public static final String FORM_CREATE_SKIN = "allaynpc:form.create.skin";
    public static final String FORM_CREATE_LOOKATPLAYER = "allaynpc:form.create.lookatplayer";
    public static final String FORM_CREATE_HELDITEM = "allaynpc:form.create.helditem";
    public static final String FORM_CREATE_HELDITEM_PLACEHOLDER = "allaynpc:form.create.helditem.placeholder";
    public static final String FORM_CREATE_COOLDOWN = "allaynpc:form.create.cooldown";
    public static final String FORM_CREATE_POSITION_LABEL = "allaynpc:form.create.position.label";
    public static final String FORM_CREATE_SUCCESS = "allaynpc:form.create.success";
    public static final String FORM_CREATE_SPAWN_FAILED = "allaynpc:form.create.spawn.failed";

    // Form - Edit menu
    public static final String FORM_EDIT_TITLE = "allaynpc:form.edit.title";
    public static final String FORM_EDIT_CONTENT = "allaynpc:form.edit.content";
    public static final String FORM_EDIT_BASIC = "allaynpc:form.edit.basic";
    public static final String FORM_EDIT_POSITION = "allaynpc:form.edit.position";
    public static final String FORM_EDIT_ARMOR = "allaynpc:form.edit.armor";
    public static final String FORM_EDIT_EMOTE = "allaynpc:form.edit.emote";
    public static final String FORM_EDIT_ACTIONS = "allaynpc:form.edit.actions";
    public static final String FORM_EDIT_NOTFOUND = "allaynpc:form.edit.notfound";

    // Form - Basic settings
    public static final String FORM_BASIC_TITLE = "allaynpc:form.basic.title";
    public static final String FORM_BASIC_UPDATED = "allaynpc:form.basic.updated";

    // Form - Position
    public static final String FORM_POSITION_TITLE = "allaynpc:form.position.title";
    public static final String FORM_POSITION_CURRENT = "allaynpc:form.position.current";
    public static final String FORM_POSITION_SETTOMINE = "allaynpc:form.position.settomine";
    public static final String FORM_POSITION_CANCEL = "allaynpc:form.position.cancel";
    public static final String FORM_POSITION_UPDATED = "allaynpc:form.position.updated";

    // Form - Armor
    public static final String FORM_ARMOR_TITLE = "allaynpc:form.armor.title";
    public static final String FORM_ARMOR_HELMET = "allaynpc:form.armor.helmet";
    public static final String FORM_ARMOR_HELMET_PLACEHOLDER = "allaynpc:form.armor.helmet.placeholder";
    public static final String FORM_ARMOR_CHESTPLATE = "allaynpc:form.armor.chestplate";
    public static final String FORM_ARMOR_CHESTPLATE_PLACEHOLDER = "allaynpc:form.armor.chestplate.placeholder";
    public static final String FORM_ARMOR_LEGGINGS = "allaynpc:form.armor.leggings";
    public static final String FORM_ARMOR_LEGGINGS_PLACEHOLDER = "allaynpc:form.armor.leggings.placeholder";
    public static final String FORM_ARMOR_BOOTS = "allaynpc:form.armor.boots";
    public static final String FORM_ARMOR_BOOTS_PLACEHOLDER = "allaynpc:form.armor.boots.placeholder";
    public static final String FORM_ARMOR_UPDATED = "allaynpc:form.armor.updated";

    // Form - Emote
    public static final String FORM_EMOTE_TITLE = "allaynpc:form.emote.title";
    public static final String FORM_EMOTE_UUID = "allaynpc:form.emote.uuid";
    public static final String FORM_EMOTE_UUID_PLACEHOLDER = "allaynpc:form.emote.uuid.placeholder";
    public static final String FORM_EMOTE_INTERVAL = "allaynpc:form.emote.interval";
    public static final String FORM_EMOTE_LABEL = "allaynpc:form.emote.label";
    public static final String FORM_EMOTE_UPDATED = "allaynpc:form.emote.updated";

    // Form - Actions
    public static final String FORM_ACTIONS_TITLE = "allaynpc:form.actions.title";
    public static final String FORM_ACTIONS_CONTENT = "allaynpc:form.actions.content";
    public static final String FORM_ACTIONS_ADD = "allaynpc:form.actions.add";
    public static final String FORM_ACTIONS_ADDED = "allaynpc:form.actions.added";
    public static final String FORM_ACTIONS_DELETED = "allaynpc:form.actions.deleted";
    public static final String FORM_ACTIONS_UPDATED = "allaynpc:form.actions.updated";

    // Form - Action edit
    public static final String FORM_ACTION_ADD_TITLE = "allaynpc:form.action.add.title";
    public static final String FORM_ACTION_EDIT_TITLE = "allaynpc:form.action.edit.title";
    public static final String FORM_ACTION_TYPE = "allaynpc:form.action.type";
    public static final String FORM_ACTION_VALUE = "allaynpc:form.action.value";
    public static final String FORM_ACTION_VALUE_PLACEHOLDER = "allaynpc:form.action.value.placeholder";
    public static final String FORM_ACTION_ASPLAYER = "allaynpc:form.action.asplayer";
    public static final String FORM_ACTION_CONTENT = "allaynpc:form.action.content";
    public static final String FORM_ACTION_EDIT = "allaynpc:form.action.edit";
    public static final String FORM_ACTION_DELETE = "allaynpc:form.action.delete";
    public static final String FORM_ACTION_BACK = "allaynpc:form.action.back";

    // Error messages
    public static final String ERROR_COMMAND_FAILED = "allaynpc:error.command.failed";
    public static final String ERROR_INVALID_EMOTE_UUID = "allaynpc:error.invalid.emote.uuid";
}

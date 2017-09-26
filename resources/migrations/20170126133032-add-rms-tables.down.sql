DROP TABLE IF EXISTS application_attachment_values CASCADE;
--;;
DROP TABLE IF EXISTS application_checkbox_values CASCADE;
--;;
DROP TABLE IF EXISTS application_form CASCADE;
--;;
DROP TABLE IF EXISTS application_form_item CASCADE;
--;;
DROP TABLE IF EXISTS application_form_item_map CASCADE;
--;;
DROP TABLE IF EXISTS application_form_item_string_values CASCADE;
--;;
DROP TABLE IF EXISTS application_form_meta CASCADE;
--;;
DROP TABLE IF EXISTS application_form_meta_map CASCADE;
--;;
DROP TABLE IF EXISTS application_license_approval_values CASCADE;
--;;
DROP TABLE IF EXISTS application_referee_invite_values CASCADE;
--;
DROP TABLE IF EXISTS application_referee_values CASCADE;
--;;
DROP TABLE IF EXISTS application_text_values CASCADE;
--;;
DROP TABLE IF EXISTS attachment CASCADE;
--;;
DROP TABLE IF EXISTS catalogue_item CASCADE;
--;;
DROP TABLE IF EXISTS catalogue_item_application CASCADE;
--;;
DROP TABLE IF EXISTS catalogue_item_application_approvers CASCADE;
--;;
DROP TABLE IF EXISTS catalogue_item_application_items CASCADE;
--;;
DROP TABLE IF EXISTS catalogue_item_application_free_comment_values CASCADE;
--;;
DROP TABLE IF EXISTS catalogue_item_application_licenses CASCADE;
--;;
DROP TABLE IF EXISTS catalogue_item_application_member_invite_values CASCADE;
--;;
DROP TABLE IF EXISTS catalogue_item_application_members CASCADE;
--;;
DROP TABLE IF EXISTS catalogue_item_application_metadata CASCADE;
--;;
DROP TABLE IF EXISTS catalogue_item_application_predecessor CASCADE;
--;;
DROP TABLE IF EXISTS catalogue_item_application_publications CASCADE;
--;;
DROP TABLE IF EXISTS catalogue_item_application_reviewers CASCADE;
--;;
DROP TABLE IF EXISTS catalogue_item_application_state CASCADE;
--;;
DROP TABLE IF EXISTS catalogue_item_application_state_reason CASCADE;
--;;
DROP TABLE IF EXISTS catalogue_item_localization CASCADE;
--;;
DROP TABLE IF EXISTS catalogue_item_predecessor CASCADE;
--;;
DROP TABLE IF EXISTS catalogue_item_state CASCADE;
--;;
DROP TABLE IF EXISTS entitlement CASCADE;
--;;
DROP TABLE IF EXISTS entitlement_ebi CASCADE;
--;;
DROP TABLE IF EXISTS entitlement_saml CASCADE;
--;;
DROP TABLE IF EXISTS entitlement_saml_migration CASCADE;
--;;
DROP TABLE IF EXISTS invitations CASCADE;
--;;
DROP TABLE IF EXISTS license CASCADE;
--;;
DROP TABLE IF EXISTS license_localization CASCADE;
--;;
DROP TABLE IF EXISTS license_references CASCADE;
--;;
DROP TABLE IF EXISTS manifestations CASCADE;
--;;
DROP TABLE IF EXISTS resource CASCADE;
--;;
DROP TABLE IF EXISTS resource_close_period CASCADE;
--;;
DROP TABLE IF EXISTS resource_licenses CASCADE;
--;;
DROP TABLE IF EXISTS resource_link_location CASCADE;
--;;
DROP TABLE IF EXISTS resource_mf_ebi_dac_target CASCADE;
--;;
DROP TABLE IF EXISTS resource_mf_saml_target CASCADE;
--;;
DROP TABLE IF EXISTS resource_prefix CASCADE;
--;;
DROP TABLE IF EXISTS resource_prefix_allow_form_editing CASCADE;
--;;
DROP TABLE IF EXISTS resource_prefix_allow_members CASCADE;
--;;
DROP TABLE IF EXISTS resource_prefix_allow_updates CASCADE;
--;;
DROP TABLE IF EXISTS resource_prefix_application CASCADE;
--;;
DROP TABLE IF EXISTS resource_prefix_certificates CASCADE;
--;;
DROP TABLE IF EXISTS resource_prefix_close_period CASCADE;
--;;
DROP TABLE IF EXISTS resource_prefix_default_form CASCADE;
--;;
DROP TABLE IF EXISTS resource_prefix_link_location CASCADE;
--;;
DROP TABLE IF EXISTS resource_prefix_mf_ebi CASCADE;
--;;
DROP TABLE IF EXISTS resource_prefix_owners CASCADE;
--;;
DROP TABLE IF EXISTS resource_prefix_refresh_period CASCADE;
--;;
DROP TABLE IF EXISTS resource_prefix_reporters CASCADE;
--;;
DROP TABLE IF EXISTS resource_prefix_state CASCADE;
--;;
DROP TABLE IF EXISTS resource_refresh_period CASCADE;
--;;
DROP TABLE IF EXISTS resource_state CASCADE;
--;;
DROP TABLE IF EXISTS user_selection_names CASCADE;
--;;
DROP TABLE IF EXISTS user_selections CASCADE;
--;;
DROP TABLE IF EXISTS workflow CASCADE;
--;;
DROP TABLE IF EXISTS workflow_approver_options CASCADE;
--;;
DROP TABLE IF EXISTS workflow_approvers CASCADE;
--;;
DROP TABLE IF EXISTS workflow_licenses CASCADE;
--;;
DROP TABLE IF EXISTS workflow_reviewers CASCADE;
--;;
DROP TABLE IF EXISTS workflow_round_min CASCADE;
--;;
DROP TABLE IF EXISTS workflow_actors CASCADE;
--;;
DROP TABLE IF EXISTS application_event CASCADE;
--;;
DROP TABLE IF EXISTS active_role CASCADE;
--;;
DROP TABLE IF EXISTS roles CASCADE;
--;;
DROP TABLE IF EXISTS users CASCADE;
--;;
DROP TYPE IF EXISTS scope;
--;;
DROP TYPE IF EXISTS itemtype;
--;;
DROP TYPE IF EXISTS approval_status;
--;;
DROP TYPE IF EXISTS license_status;
--;;
DROP TYPE IF EXISTS license_state;
--;;
DROP TYPE IF EXISTS reviewers_state;
--;;
DROP TYPE IF EXISTS application_state;
--;;
DROP TYPE IF EXISTS item_state;
--;;
DROP TYPE IF EXISTS prefix_state;
--;;
DROP TYPE IF EXISTS license_type;
--;;
DROP TYPE IF EXISTS application_event_type;
--;;
DROP TYPE IF EXISTS workflow_actor_role;
